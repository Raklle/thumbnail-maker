package api;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
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

//    private static final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    private static final HttpClient httpClient = HttpClients.createDefault();
    private static final Placeholders placeholder = new Placeholders();
    private static final String serviceAddress = "http://localhost:8080/";

    public static void uploadPhotos(List<File> files, String path) throws IOException {

        HttpPost postRequest = new HttpPost(serviceAddress);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        files.forEach(file -> builder.addBinaryBody("files", file, ContentType.IMAGE_JPEG, "files"));
        if (!Objects.equals(path, "")) builder.addTextBody("path", path);

        postRequest.setEntity(builder.build());

        HttpResponse response = httpClient.execute(postRequest);

        HttpEntity responseEntity = response.getEntity();

        if (responseEntity != null) {

            String responseContent = EntityUtils.toString(responseEntity);
            System.out.println("Response Content: " + responseContent);
        }

    }

    public static void addFolder(String name, String path) throws IOException {
        System.out.println(name);
        String requestAddress = serviceAddress + "directory";
        HttpPost postRequest = new HttpPost(requestAddress);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("name", name, ContentType.TEXT_PLAIN);
        builder.addTextBody("path", path, ContentType.TEXT_PLAIN);

        postRequest.setEntity(builder.build());

        HttpResponse response = httpClient.execute(postRequest);

        HttpEntity responseEntity = response.getEntity();

        if (responseEntity != null) {

            String responseContent = EntityUtils.toString(responseEntity);
            System.out.println("Response Content: " + responseContent);
        }
    }

    private static String getAllPhotosAddress(PhotoSize size, ArrayList<String> idList){
        String sizePath = getSizePath(size);

        StringJoiner idParam = new StringJoiner("&id=", "?id=", "");
        if (idList != null && !idList.isEmpty()) {
            idList.forEach(idParam::add);
        }

        return serviceAddress + sizePath + "/photos" + idParam.toString();
    }

    private static String getSizePath(PhotoSize size) {
        return switch (size) {
                    case LARGE -> "large";
                    case MEDIUM -> "medium";
                    case SMALL -> "small";
                    case ORIGINAL -> "";
                };
    }

    public static String getRequestAddress(PhotoSize photoSize, int pageNumber, int pageSize) {
        String sizePath = getSizePath(photoSize);
        return serviceAddress + "/photos?size=" + pageSize + "&imgSize=" + sizePath + "&page=" + pageNumber;
    }

    public static String getRequestAddress(PhotoSize photoSize, int pageNumber, int pageSize, int offset) {
        String sizePath = getSizePath(photoSize);
        return serviceAddress + "/photos?size=" + pageSize + "&imgSize=" + sizePath + "&page=" + pageNumber + "&offset=" + offset;
    }

    public static String getRequestAddress(PhotoSize photoSize, int pageNumber, int pageSize, String path) {
        String sizePath = getSizePath(photoSize);
        if (Objects.equals(path, "")) return serviceAddress + "/photos?size=" + pageSize + "&imgSize=" + sizePath + "&page=" + pageNumber;
        return serviceAddress + "/photos?size=" + pageSize + "&imgSize=" + sizePath + "&page=" + pageNumber + "&path=" + path;
    }

    public static String getRequestAddress(PhotoSize photoSize, int pageNumber, int pageSize, int offset, String path) {
        String sizePath = getSizePath(photoSize);
        return serviceAddress + "/photos?size=" + pageSize + "&imgSize=" + sizePath + "&page=" + pageNumber + "&offset=" + offset + "&path=" + path;
    }

    public static void getDirectories(ObservableList<String> list, String path) throws IOException {
        HttpGet getRequest;
        if (Objects.equals(path, "")) {
            getRequest = new HttpGet(serviceAddress + "directory");
        }
        else {
            getRequest = new HttpGet(serviceAddress + "directory?path=" + path);
        }
        HttpResponse response = httpClient.execute(getRequest);
        HttpEntity responseEntity = response.getEntity();

        if (responseEntity != null) {
            list.clear();
            list.add("..");

            List<String> toSort = new ArrayList<>();
            String responseContent = EntityUtils.toString(responseEntity);
            if (responseContent.trim().startsWith("[")) {
                JSONArray jsonArray = new JSONArray(responseContent);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String name = jsonObject.getString("name");
                    toSort.add(name);
                }
            }
            toSort.stream().sorted().forEach(list::add);
        }
    }

    public static void getAllPhotos(Gallery gallery, PhotoSize size) throws IOException {
        gallery.clear(size);
        HttpGet getRequest = new HttpGet(getAllPhotosAddress(size, gallery.getPhotosId()));

        HttpResponse response = httpClient.execute(getRequest);

        HttpEntity responseEntity = response.getEntity();

        if (responseEntity != null) {

            String responseContent = EntityUtils.toString(responseEntity);
            JSONArray jsonArray = new JSONArray(responseContent);


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String state = jsonObject.getString("state");
                if(state.equals("DONE")){
                    String id = jsonObject.getString("id");
                    String base64Image = jsonObject.getString("image");

                    byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                    gallery.addPhoto(new Image(id, imageBytes, state, placeholder));
                } else if (state.equals("TO_MINIMIZE")){
                    String id = jsonObject.getString("id");
                    byte[] empty = new byte[0];
                    var image = new Image(id, empty, state, placeholder);
                    image.setPlaceholder(size);
                    gallery.addPhoto(image);
                }
            }
        }
    }

    public static void getAllProductsPageable(Gallery gallery, PhotoSize size, String serviceAddress) throws IOException {
        gallery.clear(size);
        HttpGet getRequest = new HttpGet(serviceAddress);

        HttpResponse response = httpClient.execute(getRequest);

        HttpEntity responseEntity = response.getEntity();

        if (responseEntity != null) {

            String responseContent = EntityUtils.toString(responseEntity);
            if (!responseContent.trim().isEmpty()) {
                JSONArray jsonArray = new JSONArray(responseContent);


                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String state = jsonObject.getString("state");
                    if (state.equals("DONE")) {
                        String id = jsonObject.getString("id");
                        String base64Image = jsonObject.getString("image");

                        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                        gallery.addPhoto(new Image(id, imageBytes, state, placeholder));
                    } else if (state.equals("TO_MINIMIZE")) {
                        String id = jsonObject.getString("id");
                        byte[] empty = new byte[0];
                        var image = new Image(id, empty, state, placeholder);
                        image.setPlaceholder(size);
                        gallery.addPhoto(image);
                    }
                }
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

            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            return Optional.of(new Image(id1, imageBytes, "DONE", placeholder));

        }
        return Optional.empty();
    }
}
