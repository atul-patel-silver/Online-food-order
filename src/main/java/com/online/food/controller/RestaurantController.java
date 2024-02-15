package com.online.food.controller;

import com.online.food.helper.FileUploadHelper;
import com.online.food.modal.*;
import com.online.food.services.*;
import com.online.food.services.excel.ComplainExcelService;
import com.online.food.services.excel.OfferExcelService;
import com.online.food.services.excel.ProductExcelService;
import com.online.food.services.pdf.ComplainPdfService;
import com.online.food.services.pdf.OfferPdfService;
import com.online.food.services.pdf.ProductPdfService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/restaurant")
public class RestaurantController {
    @Autowired
    private ProductExcelService productExcelService;
    @Autowired
    private OfferExcelService offerExcelService;

    @Autowired
    private ComplainExcelService complainExcelService;

    @Autowired
    private ProductPdfService productPdfService;

    @Autowired
    private OfferPdfService offerPdfService;

    @Autowired
    private ComplainPdfService complainPdfService;

    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CityService cityService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SubCategoryService subCategoryService;
    @Autowired
    private OfferService offerService;

    @Autowired
    private ComplainService complainService;

    private Logger logger= LoggerFactory.getLogger(RestaurantController.class);


    //restaurant dash board
    @GetMapping("/index")
    public String indexPage(Model model){
        model.addAttribute("title","Restaurant | Dash-Board");
        return "restaurant/index";
    }


    //manage Product
    @GetMapping("/manage-product/{page}")
    public String manageProductPage(@PathVariable("page")int page ,Model model,Principal principal){
        try {

            Pageable pageable= PageRequest.of(page,5);

            String name = principal.getName();
            Customer customer = this.customerService.findByEmailId(name.trim());
            Restaurant restaurant = customer.getRestaurant();
            Page<Product> products = this.productService.findProductForRestaurant(restaurant.getRestaurantId(),pageable);
            model.addAttribute("products", products.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", products.getTotalPages());

            List<Category> categories = this.categoryService.findAll();
            model.addAttribute("categories",categories);
            model.addAttribute("title","Restaurant | Manage Product");

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return "restaurant/manage-product";
    }

    //get Sub Category
    @GetMapping("/get-sub-category")
    @ResponseBody
    public List<SubCategory> getSubCategory(@RequestParam("categoryId") Long categoryId){
        List<SubCategory> subCategories=null;

        try{
            subCategories=this.subCategoryService.findByCategoryId(categoryId);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return subCategories;

    }


    //add product

    @PostMapping("/saveProduct")
    @ResponseBody
    public String saveProduct(@RequestParam("productName") String productName,
                              @RequestParam("subCategoryId") String subCategoryId,
                              @RequestParam("productPrice") Long productPrice,
                              @RequestParam("productDiscription") String productDescription,
                              @RequestParam("productImage") MultipartFile productImage, Principal principal) {

        try {
            String name = principal.getName();
            Customer customer = this.customerService.findByEmailId(name);
            Restaurant restaurant = customer.getRestaurant();
            SubCategory subCategory = this.subCategoryService.findById(Long.valueOf(subCategoryId));
            Product product = Product.builder()
                    .productPrice(productPrice)
                    .productDiscription(productDescription)
                    .restaurant(restaurant)
                    .imageName(productImage.getOriginalFilename())
                    .productName(productName)
                    .subCategory(subCategory)
                    .build();

            boolean b = FileUploadHelper.uploadFile(productImage, "static/image/product-img");
            if(b){
                this.productService.save(product);
            }
            else {
                return "error";
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return "success";
    }

    //delete Product
    @PostMapping("/delete-product")
    @ResponseBody
    public String deleteProduct(@RequestParam("productId") Long productId){

        try{
            Product product = this.productService.findById(productId);

            this.productService.delete(product);
            this.logger.info("Product deleted");
        }
        catch (Exception e){
            e.printStackTrace();
            return "error";
        }
        return "succes";
    }

    //get Product
    @GetMapping("/get-product")
    @ResponseBody
    public Map<String,String> getProductData(@RequestParam("product_id") String productId){
        Map<String,String > data=new HashMap<>();
        Product product = this.productService.findById(Long.valueOf(productId));

        data.put("productId", String.valueOf(product.getProductId()));
        data.put("productName",product.getProductName());
        data.put("productPrice", String.valueOf(product.getProductPrice()));
        data.put("productDiscription",product.getProductDiscription());

        return data;
    }


    //update product
    @PostMapping("/updateProduct")
    @ResponseBody
    public String updateProduct(@RequestParam("productName") String productName,
                              @RequestParam("productId") String productId,
                              @RequestParam("productPrice") Long productPrice,
                              @RequestParam("productDiscription") String productDescription,
                              @RequestParam("productImage") MultipartFile productImage) {

        try {
            Product product = this.productService.findById(Long.valueOf(productId));
            if(productImage.isEmpty()){
                this.logger.info("Selected Photo is Empty");
            } else if (!productImage.isEmpty()) {
                if (productImage.getOriginalFilename().trim().equals(product.getImageName().trim())){
                    this.logger.info("old file and new file is equal");
                }
                else {
                    String imageName = product.getImageName();
                    boolean b = FileUploadHelper.deleteFile("static/image/product-img", imageName);
                    if (b) {
                        this.logger.info("old Photo Deleted");
                        boolean b1 = FileUploadHelper.uploadFile(productImage, "static/image/product-img");
                        if (b1) {
                            this.logger.info("new product image uploaded ");
                            product.setProductName(productImage.getName());
                        } else {
                            this.logger.info("new product image upload fail");
                        }
                    } else {
                        this.logger.info("Old Photo not deleted");
                    }
                }
            }

            product.setProductName(productName);
            product.setProductPrice(productPrice);
            product.setProductDiscription(productDescription);

            this.productService.save(product);

        }
        catch (Exception e){
            e.printStackTrace();
            return "error";
        }

        return "success";
    }


    //for offer
    @GetMapping("/manage-offer/{page}")
    public String manageOffer(@PathVariable("page")int page,Model model,Principal principal){

        try
        {
            Pageable pageable=PageRequest.of(page,5);
            Customer customer = this.customerService.findByEmailId(principal.getName());
            Restaurant restaurant = customer.getRestaurant();
            Page<Offer> offers = this.offerService.findByRestaurantID(restaurant.getRestaurantId(), pageable);
            model.addAttribute("offers", offers.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", offers.getTotalPages());

            List<Category> categories = this.categoryService.findAll();
            model.addAttribute("categories",categories);
            model.addAttribute("title","Restaurant | Manage Offer");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return "restaurant/manage-offer";
    }


//    add offer

    @PostMapping("/add-offer")
    @ResponseBody
    public String addOffer(@RequestParam("subCategoryId") Long subCategoryId,
                           @RequestParam("offerName")  String offerName,
                           @RequestParam("offerDiscount") String offerDiscount,
                           @RequestParam("startDate")LocalDate startDate,
                           @RequestParam("lastDate") LocalDate lastDate,
                           @RequestParam("offerDescription") String offerDescription,
                           Principal principal
                           ){

        try
        {
            Customer customer = this.customerService.findByEmailId(principal.getName());
            Restaurant restaurant = customer.getRestaurant();
            SubCategory subCategory = this.subCategoryService.findById(subCategoryId);
            Offer offer = Offer.builder()
                    .offerName(offerName)
                    .offerDescription(offerDescription)
                    .offerDiscount(Double.valueOf(offerDiscount))
                    .endDate(lastDate)
                    .startDate(startDate)
                    .subCategory(subCategory)
                    .restaurant(restaurant).build();
            this.offerService.save(offer);

        }
        catch (Exception e){
            e.printStackTrace();
            return "error";
        }

        return "success";
    }


//delete offer

    @PostMapping("/delete-offer")
    @ResponseBody
    public String deleteOffer(@RequestParam("offer_id") Long offer_id){
        try
        {
            System.out.println(offer_id+"-----------------------");
            Offer offer = this.offerService.findBYId(offer_id);
            this.offerService.delete(offer);
        }
        catch (Exception e){
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    @GetMapping("/get-offer")
    @ResponseBody
    public Map<String,String> getOffer(@RequestParam("offer_id") Long offer_id) throws Exception{

        Offer offer = this.offerService.findBYId(offer_id);
        Map<String,String > data=new HashMap<>();
        data.put("offerId", String.valueOf(offer.getOfferId()));
        data.put("offerName",offer.getOfferName());
        data.put("offerDiscount", String.valueOf(offer.getOfferDiscount()));
        data.put("startDate", String.valueOf(offer.getStartDate()));
        data.put("lastDate", String.valueOf(offer.getEndDate()));
        data.put("offerDescription",offer.getOfferDescription());

                return data;

    }

    @PostMapping("/update-offer")
    @ResponseBody
    public String updateOffer(@RequestParam("offerId")Long offerId,
                              @RequestParam("offerName") String offerName,
                              @RequestParam("offerDiscount") String offerDiscount,
                              @RequestParam("startDate") LocalDate startDate,
                              @RequestParam("lastDate") LocalDate lastDate,
                              @RequestParam("offerDescription") String offerDescription
    ){
      try{
          Offer offer = this.offerService.findBYId(offerId);
          offer.setOfferName(offerName);
          offer.setOfferDiscount(Double.valueOf(offerDiscount));
          offer.setStartDate(startDate);
          offer.setEndDate(lastDate);
          offer.setOfferDescription(offerDescription);
          this.offerService.save(offer);
      }
      catch (Exception e){
          e.printStackTrace();
          return "error";
      }

        return "success";
    }



    @GetMapping("/manage-complain/{page}")
    public String manageComplain(@PathVariable("page") int page,Principal principal,Model model){
        try
        {
            Customer customer = this.customerService.findByEmailId(principal.getName().trim());
            Restaurant restaurant = customer.getRestaurant();

            Pageable pageable=PageRequest.of(page,5);
            Page<Complain> complains = this.complainService.findByPainationWithRestaurantId(restaurant.getRestaurantId(),pageable);
            model.addAttribute("complains", complains.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", complains.getTotalPages());
            model.addAttribute("title","Restaurant | Manage Complain");

        }
        catch (Exception e){
           e.printStackTrace();
        }

        return "restaurant/manage-complain";
    }

    @PostMapping("/update-complain-status")
    @ResponseBody
    public String updateComplainStatus(@RequestParam("complainId")Long complainId,@RequestParam("status") int status){
        try{
            Complain complain = this.complainService.findById(complainId);

            if(status ==1){
                complain.setComplainStatus(String.valueOf(Status.INPOGRESS));
            }
            else {
                complain.setComplainStatus(String.valueOf((Status.RESOLVED)));
            }
            this.complainService.save(complain);
        }
        catch (Exception e){
            e.printStackTrace();


        }
        return "success";
    }

    @PostMapping("/reply-data")
    @ResponseBody
    public String replyDataHandle(@RequestParam("replydes") String replyDes,@RequestParam("complainId") Long complainId)
    {
        try{
            Complain complain = this.complainService.findById(complainId);
            complain.setReply(replyDes);
            complain.setReplyDate(LocalDate.now());
            this.complainService.save(complain);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "success";
    }



    //product excel
    @GetMapping("/create-product-excel")
    public ResponseEntity<byte[]> productExcelFile(Principal principal) throws Exception{




        Customer customer = this.customerService.findByEmailId(principal.getName());
        Restaurant restaurant = customer.getRestaurant();
        String fileName=restaurant.getRestaurantName()+"_Products.xlsx";

        Workbook workbook = this.productExcelService.dataToExcel(this.productService.findProductsForRestaurant(restaurant.getRestaurantId()));

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

    //offer excel
    @GetMapping("/create-offer-excel")
    public ResponseEntity<byte[]> offerExcelFile(Principal principal) throws Exception{


        Customer customer = this.customerService.findByEmailId(principal.getName());
        Restaurant restaurant = customer.getRestaurant();
        String fileName=restaurant.getRestaurantName()+"_Offers.xlsx";

        Workbook workbook = this.offerExcelService.dataToExcel(this.offerService.findByOfferRestaurantId(restaurant.getRestaurantId()));
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


    //create complain excel file
    @GetMapping("/create-complain-excel")
    public ResponseEntity<byte[]> complainExcelFile(Principal principal) throws Exception{


        Customer customer = this.customerService.findByEmailId(principal.getName());
        Restaurant restaurant = customer.getRestaurant();
        String fileName=restaurant.getRestaurantName()+"_Complains.xlsx";

        Workbook workbook = this.complainExcelService.dataToExcel(this.complainService.findByWithRestaurantId(restaurant.getRestaurantId()));

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



    //product pdf
    @GetMapping("/product-pdf-create")
    public ResponseEntity<byte[]> createProductPdf(Principal principal) throws IOException {
        try {
            Customer customer = this.customerService.findByEmailId(principal.getName());
            Restaurant restaurant = customer.getRestaurant();
            ByteArrayInputStream pdf = this.productPdfService.createPdf(this.productService.findProductsForRestaurant(restaurant.getRestaurantId()));
            byte[] pdfBytes = pdf.readAllBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", restaurant.getRestaurantName()+"_Products.pdf");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    //offer pdf
    @GetMapping("/offer-pdf-create")
    public ResponseEntity<byte[]> createOfferPdf(Principal principal) throws IOException {
        try {
            Customer customer = this.customerService.findByEmailId(principal.getName());
            Restaurant restaurant = customer.getRestaurant();
            ByteArrayInputStream pdf =this.offerPdfService.createPdf(this.offerService.findByOfferRestaurantId(restaurant.getRestaurantId()));
            byte[] pdfBytes = pdf.readAllBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", restaurant.getRestaurantName()+"_Offers.pdf");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    //complain pdf
    @GetMapping("/complain-pdf-create")
    public ResponseEntity<byte[]> createComplainPdf(Principal principal) throws IOException {
        try {
            Customer customer = this.customerService.findByEmailId(principal.getName());
            Restaurant restaurant = customer.getRestaurant();
            ByteArrayInputStream pdf = this.complainPdfService.createPdf(this.complainService.findByWithRestaurantId(restaurant.getRestaurantId()));
            byte[] pdfBytes = pdf.readAllBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", restaurant.getRestaurantName()+"_Complaines.pdf");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }



}

