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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class CommunicationHandler {

    private static final MultipartEntityBuilder builder = MultipartEntityBuilder.create();

    private static final HttpClient httpClient = HttpClients.createDefault();

    public static void uploadPhotos(List<File> files) throws IOException {

        HttpPost postRequest = new HttpPost("http://localhost:8080/photos");

        files.forEach(file -> builder.addBinaryBody("files", file, ContentType.MULTIPART_FORM_DATA, "files"));

        postRequest.setEntity(builder.build());

        HttpResponse response = httpClient.execute(postRequest);

        HttpEntity responseEntity = response.getEntity();

        if (responseEntity != null) {

            String responseContent = EntityUtils.toString(responseEntity);
            System.out.println("Response Content: " + responseContent);
        }

    }

    public static void getAllPhotos(Gallery gallery, PhotoSize size) throws IOException {

        String sizee = switch (size) {
            case LARGE -> "large";
            case MEDIUM -> "medium";
            case SMALL -> "small";
        };
        HttpGet getRequest = new HttpGet("http://localhost:8080/" + sizee +  "/photos");

        HttpResponse response = httpClient.execute(getRequest);

        HttpEntity responseEntity = response.getEntity();

        if (responseEntity != null) {

            String responseContent = EntityUtils.toString(responseEntity);
//            System.out.println("Response Content: " + responseContent);
            JSONArray jsonArray = new JSONArray(responseContent);

            gallery.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String id = jsonObject.getString("id");
                String image = jsonObject.getString("image");

                System.out.println("ID: " + id);
                System.out.println("Image: " + image);

                gallery.addPhoto(new Image(id, image.getBytes()));
                System.out.println(gallery.getPhotos().size());

            }
        }
    }

    public static Optional<Image> getOriginalPhoto(String id) throws IOException {

        HttpGet getRequest = new HttpGet("http://localhost:8080/" + id);

        HttpResponse response = httpClient.execute(getRequest);

        HttpEntity responseEntity = response.getEntity();

        if (responseEntity != null) {

            String responseContent = EntityUtils.toString(responseEntity);
//            System.out.println("Response Content: " + responseContent);
            JSONArray jsonArray = new JSONArray(responseContent);

            JSONObject jsonObject = jsonArray.getJSONObject(0);

            String id1 = jsonObject.getString("id");
            String image = jsonObject.getString("image");

            System.out.println("ID: " + id1);
            System.out.println("Image: " + image);

            return Optional.of(new Image(id1, image.getBytes()));

        }
        return Optional.empty();
    }
}
