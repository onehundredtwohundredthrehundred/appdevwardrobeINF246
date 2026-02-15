package com.example.appdevwardrobeinf246;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class ApiService {

    public static class RegisterRequest {
        private String username;
        private String password;

        public RegisterRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class ResetPasswordRequest {
        private String username;
        private String new_password;

        public ResetPasswordRequest(String username, String newPassword) {
            this.username = username;
            this.new_password = newPassword;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getNew_password() { return new_password; }
        public void setNew_password(String new_password) { this.new_password = new_password; }
    }

    public static class DeleteAccountRequest {
        private String username;
        private String password;

        public DeleteAccountRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class DeleteAccountAdminRequest {
        private String username;

        public DeleteAccountAdminRequest(String username) {
            this.username = username;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }




    public static class GetClothesRequest {
        private int user_id;
        private String search;
        private String filter_status;
        private String filter_area;
        private String filter_type;

        public GetClothesRequest(int user_id, String search, String filter_status, String filter_area, String filter_type) {
            this.user_id = user_id;
            this.search = search;
            this.filter_status = filter_status;
            this.filter_area = filter_area;
            this.filter_type = filter_type;
        }

        public int getUser_id() { return user_id; }
        public void setUser_id(int user_id) { this.user_id = user_id; }

        public String getSearch() { return search; }
        public void setSearch(String search) { this.search = search; }

        public String getFilter_status() { return filter_status; }
        public void setFilter_status(String filter_status) { this.filter_status = filter_status; }
    }

    public static class GetItemRequest {
        public int id;
        public int user_id;

        public GetItemRequest(int id, int user_id) {
            this.id = id;
            this.user_id = user_id;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public int getUser_id() { return user_id; }
        public void setUser_id(int user_id) { this.user_id = user_id; }
    }

    public static class GetItemResponse {
        private String status;
        private String message;
        private clothitem item;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public clothitem getItem() { return item; }
        public void setItem(clothitem item) { this.item = item; }
    }

    public static class AddClothRequest {
        private int user_id;
        private String name;
        private String type;
        private String area;
        private String description;
        private String image_uri;
        private Integer max_wear_count;

        public AddClothRequest(int user_id, String name, String type, String area,
                               String description, String image_uri, Integer max_wear_count) {
            this.user_id = user_id;
            this.name = name;
            this.type = type;
            this.area = area;
            this.description = description;
            this.image_uri = image_uri;
            this.max_wear_count = max_wear_count;
        }

        public AddClothRequest() {}

        public int getUser_id() { return user_id; }
        public void setUser_id(int user_id) { this.user_id = user_id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getArea() { return area; }
        public void setArea(String area) { this.area = area; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getImage_uri() { return image_uri; }
        public void setImage_uri(String image_uri) { this.image_uri = image_uri; }

        public Integer getMax_wear_count() { return max_wear_count; }
        public void setMax_wear_count(Integer max_wear_count) { this.max_wear_count = max_wear_count; }
    }

    public static class UpdateClothRequest {
        public int id;
        public int user_id;
        public String name;
        public String type;
        public String area;
        public String description;
        public String image_uri;
        public Integer max_wear_count;
        public Integer current_wear_count;

        public UpdateClothRequest() {}

        public UpdateClothRequest(int id, int user_id, String name, String type, String area,
                                  String description, String image_uri,
                                  Integer max_wear_count, Integer current_wear_count) {
            this.id = id;
            this.user_id = user_id;
            this.name = name;
            this.type = type;
            this.area = area;
            this.description = description;
            this.image_uri = image_uri;
            this.max_wear_count = max_wear_count;
            this.current_wear_count = current_wear_count;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public int getUser_id() { return user_id; }
        public void setUser_id(int user_id) { this.user_id = user_id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getArea() { return area; }
        public void setArea(String area) { this.area = area; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getImage_uri() { return image_uri; }
        public void setImage_uri(String image_uri) { this.image_uri = image_uri; }

        public Integer getMax_wear_count() { return max_wear_count; }
        public void setMax_wear_count(Integer max_wear_count) { this.max_wear_count = max_wear_count; }

        public Integer getCurrent_wear_count() { return current_wear_count; }
        public void setCurrent_wear_count(Integer current_wear_count) { this.current_wear_count = current_wear_count; }
    }

    public static class DeleteClothRequest {
        public int id;
        public int user_id;

        public DeleteClothRequest() {}

        public DeleteClothRequest(int id, int user_id) {
            this.id = id;
            this.user_id = user_id;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public int getUser_id() { return user_id; }
        public void setUser_id(int user_id) { this.user_id = user_id; }
    }

    public static class ApiResponse {
        private String status;
        private String message;
        private UserData user;
        private Integer user_id;
        private List<clothitem> clothes;
        private Integer item_id;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public UserData getUser() { return user; }
        public void setUser(UserData user) { this.user = user; }

        public Integer getUser_id() { return user_id; }
        public void setUser_id(Integer user_id) { this.user_id = user_id; }

        public List<clothitem> getClothes() { return clothes; }
        public void setClothes(List<clothitem> clothes) { this.clothes = clothes; }

        public Integer getItem_id() { return item_id; }
        public void setItem_id(Integer item_id) { this.item_id = item_id; }
    }

    public static class UserData {
        private int id;
        private String username;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }




    public static class GetOutfitsRequest {
        private int user_id;
        public GetOutfitsRequest(int user_id) { this.user_id = user_id; }
        public int getUser_id() { return user_id; }
        public void setUser_id(int user_id) { this.user_id = user_id; }
    }

    public static class OutfitSummary {
        private int id;
        private String name;
        private String description;
        private int times_worn;
        private long last_worn_timestamp;
        private String created_at;
        private String updated_at;
        private List<clothitem> clothing_items;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OutfitSummary that = (OutfitSummary) o;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public int getTimes_worn() { return times_worn; }
        public void setTimes_worn(int times_worn) { this.times_worn = times_worn; }
        public long getLast_worn_timestamp() { return last_worn_timestamp; }
        public void setLast_worn_timestamp(long last_worn_timestamp) { this.last_worn_timestamp = last_worn_timestamp; }
        public String getCreated_at() { return created_at; }
        public void setCreated_at(String created_at) { this.created_at = created_at; }
        public String getUpdated_at() { return updated_at; }
        public void setUpdated_at(String updated_at) { this.updated_at = updated_at; }
        public List<clothitem> getClothing_items() { return clothing_items; }
        public void setClothing_items(List<clothitem> clothing_items) { this.clothing_items = clothing_items; }
    }

    public static class GetOutfitsResponse {
        private String status;
        private String message;
        private List<OutfitSummary> outfits;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public List<OutfitSummary> getOutfits() { return outfits; }
        public void setOutfits(List<OutfitSummary> outfits) { this.outfits = outfits; }
    }

    public static class GetOutfitRequest {
        private int outfit_id;
        private int user_id;
        public GetOutfitRequest(int outfit_id, int user_id) { this.outfit_id = outfit_id; this.user_id = user_id; }
        public int getOutfit_id() { return outfit_id; }
        public void setOutfit_id(int outfit_id) { this.outfit_id = outfit_id; }
        public int getUser_id() { return user_id; }
        public void setUser_id(int user_id) { this.user_id = user_id; }
    }

    public static class GetOutfitResponse {
        private String status;
        private String message;
        private OutfitDetail outfit;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public OutfitDetail getOutfit() { return outfit; }
        public void setOutfit(OutfitDetail outfit) { this.outfit = outfit; }
    }

    public static class OutfitDetail {
        private int id;
        private String name;
        private String description;
        private int times_worn;
        private Long last_worn_timestamp;
        private String created_at;
        private String updated_at;
        private List<clothitem> clothing_items;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public int getTimes_worn() { return times_worn; }
        public void setTimes_worn(int times_worn) { this.times_worn = times_worn; }
        public Long getLast_worn_timestamp() { return last_worn_timestamp; }
        public void setLast_worn_timestamp(Long last_worn_timestamp) { this.last_worn_timestamp = last_worn_timestamp; }
        public String getCreated_at() { return created_at; }
        public void setCreated_at(String created_at) { this.created_at = created_at; }
        public String getUpdated_at() { return updated_at; }
        public void setUpdated_at(String updated_at) { this.updated_at = updated_at; }
        public List<clothitem> getClothing_items() { return clothing_items; }
        public void setClothing_items(List<clothitem> clothing_items) { this.clothing_items = clothing_items; }
    }

    public static class AddOutfitRequest {
        private int user_id;
        private String name;
        private String description;
        private List<Integer> clothing_ids;

        public AddOutfitRequest(int user_id, String name, String description, List<Integer> clothing_ids) {
            this.user_id = user_id;
            this.name = name;
            this.description = description;
            this.clothing_ids = clothing_ids;
        }
        public int getUser_id() { return user_id; }
        public void setUser_id(int user_id) { this.user_id = user_id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<Integer> getClothing_ids() { return clothing_ids; }
        public void setClothing_ids(List<Integer> clothing_ids) { this.clothing_ids = clothing_ids; }
    }

    public static class UpdateOutfitRequest {
        private int outfit_id;
        private int user_id;
        private String name;
        private String description;
        private List<Integer> clothing_ids;

        public UpdateOutfitRequest(int outfit_id, int user_id) {
            this.outfit_id = outfit_id;
            this.user_id = user_id;
        }
        public int getOutfit_id() { return outfit_id; }
        public void setOutfit_id(int outfit_id) { this.outfit_id = outfit_id; }
        public int getUser_id() { return user_id; }
        public void setUser_id(int user_id) { this.user_id = user_id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<Integer> getClothing_ids() { return clothing_ids; }
        public void setClothing_ids(List<Integer> clothing_ids) { this.clothing_ids = clothing_ids; }
    }

    public static class DeleteOutfitRequest {
        private int outfit_id;
        private int user_id;
        public DeleteOutfitRequest(int outfit_id, int user_id) { this.outfit_id = outfit_id; this.user_id = user_id; }
        public int getOutfit_id() { return outfit_id; }
        public void setOutfit_id(int outfit_id) { this.outfit_id = outfit_id; }
        public int getUser_id() { return user_id; }
        public void setUser_id(int user_id) { this.user_id = user_id; }
    }

    public static class WearOutfitRequest {
        private int outfit_id;
        private int user_id;
        public WearOutfitRequest(int outfit_id, int user_id) { this.outfit_id = outfit_id; this.user_id = user_id; }
        public int getOutfit_id() { return outfit_id; }
        public void setOutfit_id(int outfit_id) { this.outfit_id = outfit_id; }
        public int getUser_id() { return user_id; }
        public void setUser_id(int user_id) { this.user_id = user_id; }
    }

    public static class SimpleResponse {
        private String status;
        private String message;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // ------------------------------------------------------------------------
    // Image upload response
    // ------------------------------------------------------------------------
    public static class ImageUploadResponse {
        private String status;
        private String message;
        @SerializedName("image_url")
        private String imageUrl;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
    public static class WashClothesRequest {
        private int user_id;
        private List<Integer> clothing_ids;

        public WashClothesRequest(int user_id, List<Integer> clothing_ids) {
            this.user_id = user_id;
            this.clothing_ids = clothing_ids;
        }

        public int getUser_id() { return user_id; }
        public void setUser_id(int user_id) { this.user_id = user_id; }
        public List<Integer> getClothing_ids() { return clothing_ids; }
        public void setClothing_ids(List<Integer> clothing_ids) { this.clothing_ids = clothing_ids; }
    }

    public interface ApiInterface {
        @POST("register.php")
        Call<ApiResponse> register(@Body RegisterRequest request);

        @POST("login.php")
        Call<ApiResponse> login(@Body LoginRequest request);

        @POST("resetpassword.php")
        Call<ApiResponse> resetPassword(@Body ResetPasswordRequest request);

        @POST("deleteaccount.php")
        Call<ApiResponse> deleteAccount(@Body DeleteAccountRequest request);

        @POST("getclothes.php")
        Call<ApiResponse> getClothes(@Body GetClothesRequest request);

        @POST("listoutfits.php")
        Call<GetOutfitsResponse> getOutfits(@Body GetOutfitsRequest request);

        @POST("getoutfit.php")
        Call<GetOutfitResponse> getOutfit(@Body GetOutfitRequest request);

        @POST("addoutfit.php")
        Call<SimpleResponse> addOutfit(@Body AddOutfitRequest request);

        @POST("updateoutfit.php")
        Call<SimpleResponse> updateOutfit(@Body UpdateOutfitRequest request);

        @POST("deleteoutfit.php")
        Call<SimpleResponse> deleteOutfit(@Body DeleteOutfitRequest request);

        @POST("wearoutfit.php")
        Call<SimpleResponse> wearOutfit(@Body WearOutfitRequest request);

        @POST("getitem.php")
        Call<GetItemResponse> getItem(@Body GetItemRequest request);

        @POST("addcloth.php")
        Call<ApiResponse> addCloth(@Body AddClothRequest request);

        @POST("updatecloth.php")
        Call<ApiResponse> updateCloth(@Body UpdateClothRequest request);

        @POST("deletecloth.php")
        Call<ApiResponse> deleteCloth(@Body DeleteClothRequest request);

        @POST("deleteimages.php")
        Call<SimpleResponse> deleteImage(@Body Map<String, String> body);

        @POST("washclothes.php")
        Call<SimpleResponse> washClothes(@Body WashClothesRequest request);

        @Multipart
        @POST("imageupload.php")
        Call<ImageUploadResponse> uploadImage(@Part MultipartBody.Part image);


    }
}