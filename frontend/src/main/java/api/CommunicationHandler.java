package api;

import model.Gallery;
import model.Image;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import util.PhotoSize;
import util.Placeholders;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CommunicationHandler {

    private static final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    private static final HttpClient httpClient = HttpClients.createDefault();
    public static final Placeholders placeholder = new Placeholders();
    private static final String serviceAddress = "http://localhost:8080/";

    public static void uploadPhotos(List<File> files) throws IOException {

        HttpPost postRequest = new HttpPost(serviceAddress);

        files.forEach(file -> builder.addBinaryBody("files", file, ContentType.IMAGE_JPEG, "files"));

        postRequest.setEntity(builder.build());

        HttpResponse response = httpClient.execute(postRequest);

        HttpEntity responseEntity = response.getEntity();

        if (responseEntity != null) {

            String responseContent = EntityUtils.toString(responseEntity);
            System.out.println("Response Content: " + responseContent);
        }

    }

    private static String getAllPhotosAddress(PhotoSize size, ArrayList<String> idList){
        String sizePath =
            switch (size) {
                case LARGE -> "large";
                case MEDIUM -> "medium";
                case SMALL -> "small";
                case ORIGINAL -> "";
            };

        StringJoiner idParam = new StringJoiner("&id=", "?id=", "");
        if (idList != null && !idList.isEmpty()) {
            idList.forEach(idParam::add);
        }
//        System.out.println(serviceAddress + sizePath + "/photos" + idParam.toString());
        return serviceAddress + sizePath + "/photos" + idParam.toString();
    }
    public static void getAllPhotos(Gallery gallery, PhotoSize size) throws IOException {
        gallery.clear(size);
        HttpGet getRequest = new HttpGet(getAllPhotosAddress(size, gallery.getPhotosId()));

        HttpResponse response = httpClient.execute(getRequest);

        HttpEntity responseEntity = response.getEntity();

        if (responseEntity == null) return;
        String responseContent = EntityUtils.toString(responseEntity);
//            System.out.println("Response Content: " + responseContent);
        JSONArray jsonArray = new JSONArray(responseContent);


        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String state = jsonObject.getString("state");
            if(state.equals("DONE")){
                String id = jsonObject.getString("id");
                String base64Image = jsonObject.getString("image");

//                    System.out.println("ID: " + id);
//                    System.out.println("Image: " + base64Image);
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                gallery.addPhoto(new Image(id, imageBytes, state));
//                    System.out.println(gallery.getPhotos().size());
            }else if (state.equals("TO_MINIMIZE")){
                String id = jsonObject.getString("id");
                byte[] empty = new byte[0];
                var image = new Image(id, empty, state);
                image.setPlaceholder(size, placeholder);
                gallery.addPhoto(image);
            }



        }

    }

    public static Optional<Image> getOriginalPhoto(String id) throws IOException {

        HttpGet getRequest = new HttpGet(serviceAddress + id);

        HttpResponse response = httpClient.execute(getRequest);

        HttpEntity responseEntity = response.getEntity();

        if (responseEntity != null) {

            String responseContent = EntityUtils.toString(responseEntity);
            JSONObject jsonObject = new JSONObject(responseContent);

            String id1 = jsonObject.getString("id");
            String base64Image = jsonObject.getString("image");

//            System.out.println("ID: " + id1);
//            System.out.println("Image: " + base64Image);
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            return Optional.of(new Image(id1, imageBytes, "DONE"));

        }
        return Optional.empty();
    }


}
