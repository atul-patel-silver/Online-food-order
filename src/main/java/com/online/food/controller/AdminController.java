package com.online.food.controller;

import com.online.food.modal.*;
import com.online.food.services.*;
import com.online.food.services.excel.CityExcelService;
import com.online.food.services.pdf.CityPdfService;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import  java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private CityService cityService;
    @Autowired
    private AreaService areaService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SubCategoryService subCategoryService;
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private OfferService offerService;

    @Autowired
    private CustomerService customerService;
    @Autowired
    private ComplainService complainService;
    @Autowired
    private CityExcelService cityExcelService;
    @Autowired
    private CityPdfService cityPdfService;

    @GetMapping("/index")
    public String indexPage(Model model) {
        try {
            model.addAttribute("title", "Admin | DashBoard");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "admin/index";
    }

    //for city
    @GetMapping("manage-city/{page}")
    public String manageCityPage(@PathVariable("page") int page, Model model) {

        try {
            Pageable pageable = PageRequest.of(page, 5);
            Page<City> cities = this.cityService.findAllCityByPagination(pageable);

            model.addAttribute("cities", cities.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", cities.getTotalPages());
            model.addAttribute("title", "Manage-City");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "admin/manage-city";
    }

    //add city data
    @PostMapping("/add-cityData")
    @ResponseBody
    public String addCityData(@RequestBody Map<String, String> data) {
        try {

            String cityName=data.get("cityName");
            String cityDiscription=data.get("cityDiscription");
            City byCityName = this.cityService.findByCityName(cityName);
            if (byCityName == null) {
                City build = City.builder().cityDiscription(cityDiscription).cityName(cityName).build();
                this.cityService.save(build);
            } else {
                this.logger.info("already present this city");
                return "already present this city";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "success";

    }

    @GetMapping("/get-city")
    @ResponseBody
    public City getCity(@RequestParam("cityId") String cityId){


        City byId = this.cityService.findById(Long.valueOf(cityId));

        return byId;
    }
    //update city data

    @PostMapping("/update-cityData")
    @ResponseBody
    public String updateCityData(@RequestBody Map<String, String> data) {


        try {
            String cityId=data.get("cityId");
            String cityName=data.get("cityName");
            String cityDiscription=data.get("cityDiscription");
            City city = this.cityService.findById(Long.valueOf(cityId));
            city.setCityName(cityName);
            city.setCityDiscription(cityDiscription);

            this.cityService.save(city);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }


    //delete city
    @PostMapping("/delete-city")
    @ResponseBody
    public String deleteCity(@RequestParam String city_id) {
        try {

            City city = this.cityService.findById(Long.valueOf(city_id));
            this.cityService.delete(city);
            this.logger.info("City Has Been Deleted");

        } catch (Exception e) {
            e.getMessage();

            this.logger.info("Something Went Wrong during delete a City !!!");
        }
        return "City deleted successfully";
    }


    //for area
    @GetMapping("/manage-area/{page}")
    public String manageAreaPage(@PathVariable("page") int page, Model model) {

        try {
            Pageable pageable = PageRequest.of(page, 5);
            Page<Area> areas = this.areaService.findAreaByPagination(pageable);

            List<City> cities = this.cityService.findAll();
            model.addAttribute("cities", cities);


            model.addAttribute("areas", areas.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", areas.getTotalPages());
            model.addAttribute("title", "Manage-Area");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "admin/manage-area";
    }

    //add area
    @PostMapping("/add-AreaData")
    @ResponseBody
    public String addAreaData(@RequestBody Map<String, String> data) {
        try {


            String cityId = data.get("cityId");
            String areaName = data.get("areaName");
            String areaDiscription = data.get("areaDiscription");
            Area areaByName = this.areaService.getAreaByName(areaName);
            if (areaByName == null) {

                City city = this.cityService.findById(Long.valueOf(cityId));

                Area area = Area.builder().areaDiscription(areaDiscription).areaName(areaName).city(city).build();
                this.areaService.save(area);

                this.logger.info("added Area");
            } else {
                return "already present this area";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "success";
    }

    @GetMapping("/get-area")
    @ResponseBody
    public Map<String,String> getArea(@RequestParam("areaId") String areaId){


        Area byId = this.areaService.findById(Long.valueOf(areaId));
           Map<String,String> data=new HashMap<>();
           data.put("cityId", String.valueOf(byId.getCity().getCityId()));
           data.put("cityName",byId.getCity().getCityName());
           data.put("areaId", String.valueOf(byId.getAreaId()));
           data.put("areaName",byId.getAreaName());
           data.put("areaDiscription",byId.getAreaDiscription());
           this.logger.info("retrive area");

        return data;
    }

    //update area

    @PostMapping("/update-areaData")
    @ResponseBody
    public String updateArea(@RequestBody  Map<String,String> data){
        try{
            String cityId=data.get("cityId");
           String areaId= data.get("areaId");
           String areaName=data.get("areaName");
          String areaDiscription=data.get("areaDiscription");
            City city = this.cityService.findById(Long.valueOf(cityId));
            Area area = this.areaService.findById(Long.valueOf(areaId));
            area.setAreaName(areaName);
            area.setAreaDiscription(areaDiscription);
            area.setCity(city);
            this.areaService.save(area);
            this.logger.info("Update City");


        }
        catch (Exception e){
            e.printStackTrace();
            return "something Went wrong";

        }
        return "success";
    }
    //delete area
    @PostMapping("/delete-area")
    @ResponseBody
    public String deleteArea(@RequestParam String area_id) {
        try {

            Area byId = this.areaService.findById(Long.valueOf(area_id));
            this.areaService.delete(byId);
            this.logger.info("Area Has Been Deleted");

        } catch (Exception e) {
            e.getMessage();

            this.logger.info("Something Went Wrong during delete a Area !!!");
        }
        return "Area deleted successfully";
    }


    //manage area
    @GetMapping("/manage-category/{page}")
    public String manageCategoryPage(@PathVariable("page") int page, Model model) {

        try {
            Pageable pageable = PageRequest.of(page, 5);
            Page<Category> categories = this.categoryService.findByPagination(pageable);


            model.addAttribute("categories", categories.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", categories.getTotalPages());
            model.addAttribute("title", "Manage-Category");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "admin/manage-category";
    }


    //add area
    @PostMapping("/add-categoryData")
    @ResponseBody
    public String addCategoryData(@RequestBody Map<String, String> data) {
        try {
            String categoryName = data.get("categoryName");
            String categoryDiscription = data.get("categoryDiscription");

            Category byCategoryName = this.categoryService.getByCategoryName(categoryName);
            if (byCategoryName == null) {
                Category build = Category.builder().categoryName(categoryName).categoryDiscription(categoryDiscription).build();
                this.categoryService.save(build);
                this.logger.info("added Category");
            } else {
                return "already present this category";
            }


        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

        return "success";
    }

    //get Category

    @GetMapping("/get-category")
    @ResponseBody
    public Category getCategory(@RequestParam("categoryId") String categoryId) throws Exception{


        Category category = this.categoryService.findById(Long.valueOf(categoryId));

        return category;
    }


    //update category data handle
@PostMapping("/update-categoryData")
@ResponseBody
    public String handleUpdateCategoryData(@RequestBody Map<String,String> data){
        try{
            String categoryId=data.get("categoryId");
            String categoryName=data.get("categoryName");
            String categoryDiscription=data.get("categoryDiscription");

            Category category = this.categoryService.findById(Long.valueOf(categoryId));
            category.setCategoryDiscription(categoryDiscription);
            category.setCategoryName(categoryName);
            this.categoryService.save(category);
            this.logger.info("Update Category");

        }catch (Exception e){
            e.printStackTrace();
        }
        return "success";
    }


    //delete category
    @PostMapping("/delete-category")
    @ResponseBody
    public String deleteCategory(@RequestParam String category_id) {
        try {

            Category category = this.categoryService.findById(Long.valueOf(category_id));
            this.categoryService.delete(category);
            this.logger.info("Category Has Been Deleted");

        } catch (Exception e) {
            e.getMessage();

            this.logger.info("Something Went Wrong during delete a Category !!!");
        }
        return "Category deleted successfully";
    }


    //manage sub category

    @GetMapping("/manage-sub-category/{page}")
    public String manageSubCategoryPage(@PathVariable("page") int page, Model model) {

        try {
            Pageable pageable = PageRequest.of(page, 5);

            List<Category> categories = this.categoryService.findAll();
            Page<SubCategory> subCategories = this.subCategoryService.findByPagination(pageable);
            model.addAttribute("categories", categories);
            model.addAttribute("subCategories", subCategories.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", subCategories.getTotalPages());
            model.addAttribute("title", "Manage-Category");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "admin/manage-sub-category";
    }


    //add sub category
    @PostMapping("/add-subcategoryData")
    @ResponseBody
    public String addSubCategoryData(@RequestBody Map<String, String> data) {
        try {
            String categoryId = data.get("categoryId");
            String subCategoryName = data.get("subCategoryName");
            String subCategoryDiscription=data.get("subCategoryDiscription");



            SubCategory subCategory = this.subCategoryService.findBySubCategoryName(subCategoryName);
            if (subCategory == null) {
                Category category = this.categoryService.findById(Long.valueOf(categoryId));
                SubCategory subCategory1 = SubCategory.builder().subCategoryDiscription(subCategoryDiscription).subCategoryName(subCategoryName).category(category).build();
                    this.subCategoryService.save(subCategory1);
                this.logger.info("added SubCategory");
            } else {
                return "already present this subcategory";
            }


        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

        return "success";
    }


    //get subCategory data


    @GetMapping("/get-sub-category")
    @ResponseBody
    public Map<String,String> getSubCategoryData(@RequestParam("subCategoryId") String subCategoryId) throws Exception{

        SubCategory subCategory = this.subCategoryService.findById(Long.valueOf(subCategoryId));
            Map<String,String> data=new HashMap<>();
            data.put("categoryId", String.valueOf(subCategory.getCategory().getCategoryId()));
            data.put("categoryName",subCategory.getCategory().getCategoryName());
            data.put("subCategoryId", String.valueOf(subCategory.getSubCategoryId()));
            data.put("subCategoryName",subCategory.getSubCategoryName());
            data.put("subCategoryDiscription",subCategory.getSubCategoryDiscription());

            return data;
    }


    //update sub category
    @PostMapping("/update-sub-categoryData")
    @ResponseBody
    public String handleSubCategoryUpdateData(@RequestBody Map<String,String> data){
        try{
            String subCategoryId=data.get("subCategoryId");
            String categoryId=data.get("categoryId");
            String subCategoryName=data.get("subCategoryName");
            String subCategoryDiscription=data.get("subCategoryDiscription");

            Category category = this.categoryService.findById(Long.valueOf(categoryId));
            SubCategory subCategory = this.subCategoryService.findById(Long.valueOf(subCategoryId));

            subCategory.setCategory(category);
            subCategory.setSubCategoryName(subCategoryName);
            subCategory.setSubCategoryDiscription(subCategoryDiscription);
            this.subCategoryService.save(subCategory);
            this.logger.info("update sub category");
        }catch (Exception e){
            e.printStackTrace();
        }

        return "success";
    }

    //delete sub category

    @PostMapping("/delete-sub-category")
    @ResponseBody
    public String deleteSubCategory(@RequestParam String sub_category_id) {
        try {

            SubCategory subCategory = this.subCategoryService.findById(Long.valueOf(sub_category_id));
            this.subCategoryService.delete(subCategory);
            this.logger.info("SubCategory Has Been Deleted");

        } catch (Exception e) {
            e.getMessage();

            this.logger.info("Something Went Wrong during delete a SubCategory !!!");
        }
        return "SubCategory deleted successfully";
    }




    //for restaurant page
    @GetMapping("/manage-restaturant/{page}")
    public String managerestaurantPage(@PathVariable("page")int page,Model model){
        try{
            Pageable pageable = PageRequest.of(page, 5);
            Page<Restaurant> restaurants = this.restaurantService.findByPagination(pageable);
            model.addAttribute("restaurants", restaurants.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", restaurants.getTotalPages());
            model.addAttribute("title", "Restaurant");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return "admin/manage-restaurant";
    }

//show all offer

    @GetMapping("/manage-offer/{page}")
    public String manageOffer(@PathVariable("page")int page, Model model){

        try
        {
            Pageable pageable=PageRequest.of(page,5);
            Page<Offer> offers = this.offerService.findAllWithPagination(pageable);
            model.addAttribute("offers", offers.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", offers.getTotalPages());


            model.addAttribute("title","Admin | Offer");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return "admin/manage-offer";
    }

    @GetMapping("/manage-complain/{page}")
    public String manageComplain(@PathVariable("page") int page,Model model){
        try
        {


            Pageable pageable=PageRequest.of(page,5);
            Page<Complain> complains = this.complainService.findAllBYPagination(pageable);
            model.addAttribute("complains", complains.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", complains.getTotalPages());
            model.addAttribute("title","Admin | Manage Complain");

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return "admin/manage-complain";
    }


    //for excel file


    @GetMapping("/create-city-excel")
    public ResponseEntity<byte[]> cityExcelFile() throws Exception{


            String fileName="Food_Order_Cities";

            List<City> city = this.cityService.findAll();
        Workbook workbook = this.cityExcelService.dataToExcel(city);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",fileName);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }


    //pdf service

    @GetMapping("/city-pdf-create")
    public ResponseEntity<byte[]> careateCityPdf() throws IOException {
        try {
            List<City> cities = this.cityService.findAll();
            ByteArrayInputStream pdf = this.cityPdfService.createPdf(cities);
            byte[] pdfBytes = pdf.readAllBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Food_Order_Cities.pdf");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }


}
