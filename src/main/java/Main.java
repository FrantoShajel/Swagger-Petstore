import com.mysql.cj.protocol.Resultset;
import io.swagger.oas.models.ExternalDocumentation;
import io.swagger.oas.models.OpenAPI;
import io.swagger.oas.models.Operation;
import io.swagger.oas.models.info.Contact;
import io.swagger.oas.models.info.Info;
import io.swagger.oas.models.info.License;
import io.swagger.oas.models.media.Content;
import io.swagger.oas.models.media.MediaType;
import io.swagger.oas.models.media.Schema;
import io.swagger.oas.models.parameters.Parameter;
import io.swagger.oas.models.parameters.RequestBody;
import io.swagger.oas.models.responses.ApiResponse;
import io.swagger.oas.models.responses.ApiResponses;
import io.swagger.oas.models.security.SecurityRequirement;
import io.swagger.oas.models.tags.Tag;

import java.sql.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/swagger_petstore";
        String uname = "root";
        String pass = "frash";

        Connection con = null;
        Statement stmt = null;
        Statement stmt1 = null;
        Statement stmt2 = null;
        Statement stmt3 = null;
//        Statement stmt4 = null;
//        Statement stmt5 = null;

        OpenAPI petstore = new OpenAPI();

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("Connecting to a selected database...");
            con = DriverManager.getConnection(url, uname, pass);
            System.out.println("Connected database successfully...");
            System.out.println("Creating statement...");
            stmt = con.createStatement();
            stmt1 = con.createStatement();
            stmt2 = con.createStatement();
            stmt3 = con.createStatement();
//            stmt4 = con.createStatement();
//            stmt5 = con.createStatement();

            //info:
            System.out.println("Info started");
            String query = "SELECT * FROM info";
            ResultSet info_rs = stmt.executeQuery(query);

            Info petstore_info = new Info();

            info_rs.next();
            petstore_info.setDescription(info_rs.getString("Description"));
            petstore_info.setVersion(info_rs.getString("InfoVersion"));
            petstore_info.setTitle(info_rs.getString("InfoTitle"));
            petstore_info.setTermsOfService(info_rs.getString("InfoTermsOfService"));
            Contact info_contact = new Contact();
            info_contact.setEmail(info_rs.getString("InfoContactEmail"));
            petstore_info.setContact(info_contact);
            License info_license = new License();
            info_license.setName(info_rs.getString("InfoLicenseName"));
            info_license.setUrl(info_rs.getString("InfoLicenseUrl"));
            petstore_info.setLicense(info_license);

            petstore.setInfo(petstore_info);

//            System.out.println(petstore.getInfo());
            info_rs.close();
            System.out.println("Info Ended");


            //Tags:
            System.out.println("Tags Started");

            String tags_query = "SELECT * FROM tags";
            ResultSet tags_rs = stmt.executeQuery(tags_query);

            List<Tag> Tags = new ArrayList<Tag>();

            while(tags_rs.next()) {

                Tag tag_element = new Tag();
                tag_element.setName(tags_rs.getString("Name"));
                tag_element.setDescription(tags_rs.getString("Description"));
                ExternalDocumentation extDocs = new ExternalDocumentation();
                extDocs.setDescription(tags_rs.getString("ExternalDocsDescription"));
                extDocs.setUrl(tags_rs.getString("ExternalDocsUrl"));
                tag_element.setExternalDocs(extDocs);

                Tags.add(tag_element);
            }

            petstore.setTags(Tags);

//            System.out.println(petstore.getTags());
            tags_rs.close();

            System.out.println("Tags Ended");

            //Paths:
            System.out.println("Paths Started");
            String paths_query = "SELECT * FROM Paths";
            ResultSet paths_rs = stmt.executeQuery(paths_query);
//            String path = "";

//            while (paths_rs.next()) {
                paths_rs.next();

                Operation pet_put = new Operation();

                int pathID = paths_rs.getInt("PathID");
                String pathName = paths_rs.getString("Path");
                String summary = paths_rs.getString("Summary");
                String description = paths_rs.getString("Description");
                String operationID = paths_rs.getString("OperationId");
                boolean deprecated = paths_rs.getBoolean("Deprecated");
                String x_codegen = paths_rs.getString("x-codegen-request-body-name");

                pet_put.setSummary(summary);
                pet_put.setDescription(description);
                pet_put.setOperationId(operationID);
                pet_put.setDeprecated(deprecated);


                        //tags
            System.out.println("Path Tags started");
                        String paramTags_query = "SELECT Paths.PathID, TagID, Tag FROM Paths \n" +
                                "LEFT JOIN PathTags ON Paths.PathID = PathTags.PathID\n" +
                                "WHERE Paths.PathID = 0\n" +
                                "ORDER BY Paths.PathID";
                        ResultSet paramTags_rs = stmt1.executeQuery(paramTags_query);

                        List<String> tag_list = new ArrayList<String>();
                        while(paramTags_rs.next()) {
                            String pathTagElement = paramTags_rs.getString("Tag");
                            tag_list.add(pathTagElement);
                        }

                        pet_put.setTags(tag_list);

                        paramTags_rs.close();
            System.out.println("Path Tags Ended");


                //parameters
            System.out.println("Parameter Started");
                String parameter_query = "SELECT * FROM Paths \n" +
                        "LEFT JOIN Parameters ON Paths.PathID = Parameters.PathID\n" +
                        "WHERE Paths.PathID = 0\n" +
                        "ORDER BY Paths.PathID";
                ResultSet parameters_rs = stmt1.executeQuery(parameter_query);

                List<Parameter> parameter_list = new ArrayList<Parameter>();

                while(parameters_rs.next()) {

                    Parameter param = new Parameter();

                    String param_name = parameters_rs.getString("Name");
                    String param_in = parameters_rs.getString("In");
                    String param_desc = parameters_rs.getString("Param_Description");
                    boolean param_required = parameters_rs.getBoolean("IsRequired");
                    String param_style = parameters_rs.getString("Style");   //cannot set style
                    boolean param_explode = parameters_rs.getBoolean("Explode");
                    String param_schemaType = parameters_rs.getString("SchemaType");
                    String param_schemaFormat = parameters_rs.getString("SchemaFormat");
                    int param_schemaMax = parameters_rs.getInt("SchemaMaximum");
                    int param_schemaMin = parameters_rs.getInt("SchemaMinimum");

                    param.setName(param_name);
                    param.setIn(param_in);
                    param.setDescription(param_desc);
                    param.setRequired(param_required);
                    //param.setStyle();
                    param.setExplode(param_explode);

                    Schema param_schema = new Schema();

                    param_schema.setType(param_schemaType);
                    param_schema.setFormat(param_schemaFormat);
                    param_schema.setMaxItems(param_schemaMax);
                    param_schema.setMinItems(param_schemaMin);

                    Map<String,Schema> paramSchemaItems = new HashMap<String,Schema>();
                    String parameterItems_query = "SELECT * FROM Paths \n" +
                            "LEFT JOIN ParameterItems ON Paths.PathID = ParameterItems.PathID\n" +
                            "WHERE Paths.PathID = 0 AND ParameterID = 0\n" +
                            "ORDER BY Paths.PathID;";
                    ResultSet parameterItems_rs = stmt2.executeQuery(parameterItems_query);
                    System.out.println("Parameter items Started");

                    Schema<String> parameterItems_schema = new Schema<String>();
                    List<String> parameterItemsEnum = new ArrayList<String>();

                    while(parameterItems_rs.next()) {
                        String paramItemType = parameterItems_rs.getString("Type");
                        if(paramItemType != null) {
                            parameterItems_schema.setType(paramItemType);
                        }

                        String enum_item = parameterItems_rs.getString("Item");

                        if(parameterItems_rs.getBoolean("Default")) {
                            parameterItems_schema.setDefault(enum_item);
                        }


                        parameterItemsEnum.add(enum_item);

                    }


                    parameterItems_rs.close();

                    System.out.println("Parameter Items Ended");

                    parameterItems_schema.setEnum(parameterItemsEnum);
//                    System.out.println(parameterItems_schema.getEnum());

                    paramSchemaItems.put("items", parameterItems_schema);
                    param_schema.setProperties(paramSchemaItems);
                    param.setSchema(param_schema);

                    parameter_list.add(param);
                    pet_put.setParameters(parameter_list);


                }

                parameters_rs.close();

            System.out.println("Parameters Ended");

//            System.out.println(pet_put.getParameters());

                //request body
            System.out.println("Request Body Started");
                String requestBody_query = "SELECT * FROM Paths\n" +
                        "LEFT JOIN RequestBody ON Paths.PathID = RequestBody.PathID\n" +
                        "WHERE Paths.PathID = 0\n" +
                        "ORDER BY Paths.PathID;";
                ResultSet requestBody_rs = stmt1.executeQuery(requestBody_query);

                RequestBody requestBody_obj = new RequestBody();

                while(requestBody_rs.next()) {
                    String RBDescription = requestBody_rs.getString("Req_Description");
                    boolean RBIsRequired = requestBody_rs.getBoolean("Req_IsRequired");

                    requestBody_obj.setDescription(RBDescription);
                    requestBody_obj.setRequired(RBIsRequired);

                    String requestBodyContent_query = "SELECT * FROM Paths\n" +
                            "LEFT JOIN RequestBodyContent ON Paths.PathID = RequestBodyContent.PathID\n" +
                            "WHERE Paths.PathID = 0\n" +
                            "ORDER BY Paths.PathID;";
                    ResultSet requestBodyContent_rs = stmt2.executeQuery(requestBodyContent_query);

                    System.out.println("Request Body Content Started");

                    Content requestBodyContent_obj = new Content();


                    if(requestBodyContent_rs.next()) {
                        RBCLoop:
                        while(true){
                            String RBContentName = requestBodyContent_rs.getString("ContentName");
                            String RBContentProperty = requestBodyContent_rs.getString("SchemaProperties");
                            String RBContentDescription = requestBodyContent_rs.getString("ContentDescription");
                            String RBContentType = requestBodyContent_rs.getString("Type");
                            String RBContentFormat = requestBodyContent_rs.getString("Format");
                            String RBContentRef = requestBodyContent_rs.getString("Ref");

//                        if(RBContentName == null) {
                            //add to previous schema
//                        } else {
                            //create new schema
                            MediaType requestBodyContent_media = new MediaType();
                            Schema requestBodyContent_mainSchema = new Schema();
                            if(RBContentProperty == null){
                                requestBodyContent_mainSchema.setDescription(RBContentDescription);
                                requestBodyContent_mainSchema.setType(RBContentType);
                                requestBodyContent_mainSchema.setFormat(RBContentFormat);
                                requestBodyContent_mainSchema.set$ref(RBContentRef);
                                requestBodyContent_media.setSchema(requestBodyContent_mainSchema);
                                requestBodyContent_obj.addMediaType(RBContentName, requestBodyContent_media);
                            } else{
                                Map<String,Schema> RBContentProperties_map = new HashMap<String,Schema>();
                                Schema requestBodyContent_propSchema = new Schema();
                                requestBodyContent_propSchema.setDescription(RBContentDescription);
                                requestBodyContent_propSchema.setType(RBContentType);
                                requestBodyContent_propSchema.setFormat(RBContentFormat);
                                requestBodyContent_propSchema.set$ref(RBContentRef);
                                RBContentProperties_map.put(RBContentProperty, requestBodyContent_propSchema);

                                if(requestBodyContent_rs.next() ){
                                    if(requestBodyContent_rs.getString("ContentName") == null) {
                                        String RBContentProperty2 = requestBodyContent_rs.getString("SchemaProperties");
                                        String RBContentDescription2 = requestBodyContent_rs.getString("ContentDescription");
                                        String RBContentType2 = requestBodyContent_rs.getString("Type");
                                        String RBContentFormat2 = requestBodyContent_rs.getString("Format");
                                        String RBContentRef2 = requestBodyContent_rs.getString("Ref");

                                        Schema requestBodyContent_propSchema2 = new Schema();
                                        requestBodyContent_propSchema2.setDescription(RBContentDescription2);
                                        requestBodyContent_propSchema2.setType(RBContentType2);
                                        requestBodyContent_propSchema2.setFormat(RBContentFormat2);
                                        requestBodyContent_propSchema2.set$ref(RBContentRef2);
                                        RBContentProperties_map.put(RBContentProperty2, requestBodyContent_propSchema2);
                                    }
                                } else {
                                    break RBCLoop;
                                }
                                requestBodyContent_mainSchema.setProperties(RBContentProperties_map);
                                requestBodyContent_media.setSchema(requestBodyContent_mainSchema);
                                requestBodyContent_obj.addMediaType(RBContentName, requestBodyContent_media);
                                continue;
                            }

//                        }
                            if(requestBodyContent_rs.next()){
                                continue;
                            } else {
                                break;
                            }
                        }//end of RBcontent while
                        requestBody_obj.setContent(requestBodyContent_obj);
                    }

                    requestBodyContent_rs.close();

                    System.out.println("Request Body Content Ended");




                }

            requestBody_rs.close();

            System.out.println("Request Body Ended");


                //responses
                String response_query = "SELECT * FROM Paths\n" +
                        "LEFT JOIN Responses ON Paths.PathID = Responses.PathID\n" +
                        "WHERE Paths.PathID = 0\n" +
                        "ORDER BY Paths.PathID;";
                ResultSet Response_rs = stmt1.executeQuery(response_query);

            System.out.println("Response Started");

                ApiResponses Response_map = new ApiResponses();

                while(Response_rs.next()) {

                    ApiResponse Response_obj = new ApiResponse();

                    int ResponseID = Response_rs.getInt("ResponseID");
                    String HTTPStatus = Response_rs.getString("HTTPStatus");
                    String responseDescription = Response_rs.getString("ResponseDescription");

                    Response_obj.setDescription(responseDescription);

                    String ResponseContent_query = "SELECT * FROM ResponseContent\n" +
                            "WHERE ResponseID = 0 AND PathID = 0\n" +
                            "ORDER BY ResponseContentID;";
                    ResultSet ResponseContent_rs = stmt2.executeQuery(ResponseContent_query);

                    System.out.println("Response Content Started");

                    Content ResponseContent = new Content();

                    while(ResponseContent_rs.next()) {
                        MediaType ResponseContent_media = new MediaType();
                        Schema ResponseContent_mainSchema = new Schema();


                        int RCid = ResponseContent_rs.getInt("ResponseContentID");
                        String RCName = ResponseContent_rs.getString("ContentItem");
                        String RCType = ResponseContent_rs.getString("SchemaType");
                        String RCRef = ResponseContent_rs.getString("SchemaRef");

                        ResponseContent_mainSchema.setType(RCType);
                        ResponseContent_mainSchema.set$ref(RCRef);


                        String ResponseContentItem_query = "SELECT * FROM ResponseContentItem\n" +
                                "WHERE ResponseContentID = 0 AND PathID = 0 AND ResponseID = 0\n" +
                                "ORDER BY ResponseContentID;";
                        ResultSet ResponseContentItem_rs = stmt3.executeQuery(ResponseContentItem_query); //stmt3

                        System.out.println("Response Content Item Started");

                        Map<String, Schema> ResponseContentProperties_map = new HashMap<String, Schema>();

                        while(ResponseContentItem_rs.next()) {

                            Schema ResponseContent_propSchema = new Schema();

                            String RCItemName = ResponseContentItem_rs.getString("Item");
                            String RCItemType = ResponseContentItem_rs.getString("Type");
                            String RCItemRef = ResponseContentItem_rs.getString("$Ref");

                            ResponseContent_propSchema.setType(RCItemType);
                            ResponseContent_propSchema.set$ref(RCItemRef);

                            ResponseContentProperties_map.put(RCItemName, ResponseContent_propSchema);
                        }

                        ResponseContentItem_rs.close();
                        System.out.println("Response Content Item Ended");

                        ResponseContent_mainSchema.setProperties(ResponseContentProperties_map);
                        ResponseContent_media.setSchema(ResponseContent_mainSchema);
                        ResponseContent.addMediaType(RCName, ResponseContent_media);
                    }

                    ResponseContent_rs.close();
                    System.out.println("Response Content Ended");

                    Response_obj.setContent(ResponseContent);
                    Response_map.put(HTTPStatus, Response_obj);
                }

            Response_rs.close();
            System.out.println("Response Ended");

                pet_put.setResponses(Response_map);




                //auth
                String Security_query = "SELECT PathID, PathAuth.AuthID, PathAuth.ScopeID, AuthName, ScopeName FROM PathAuth\n" +
                        "LEFT JOIN Auth ON PathAuth.AuthID = Auth.AuthID\n" +
                        "LEFT JOIN Scope ON PathAuth.ScopeID = Scope.ScopeID\n" +
                        "WHERE PathID = 0;";
                ResultSet Security_rs = stmt1.executeQuery(Security_query);
            System.out.println("SEcurity Started");

                List<SecurityRequirement> SecurityList = new ArrayList<SecurityRequirement>();

                SecurityRequirement Security_map = new SecurityRequirement();

                Security_rs.next();

                security:
                while(true){
                    List<String> ScopeNames = new ArrayList<String>();

                    int SecurityAuthID = Security_rs.getInt("AuthID");
                    String SecurityAuthName = Security_rs.getString("AuthName");
                    String SecurityScopeName = Security_rs.getString("ScopeName");

                    ScopeNames.add(SecurityScopeName);

                    if(Security_rs.next()) {
                        while (true) {
                            if(Security_rs.getInt("AuthID") == SecurityAuthID) {
                                ScopeNames.add(Security_rs.getString("ScopeName"));
                                if(Security_rs.next()){
                                    continue;
                                }else {
                                    Security_map.put(SecurityAuthName, ScopeNames);
                                    break security;
                                }

                            } else {
                                Security_map.put(SecurityAuthName, ScopeNames);
                                break;
                            }
                        }

                    } else {
                        Security_map.put(SecurityAuthName, ScopeNames);
                        break;
                    }
                }

            Security_rs.close();
            System.out.println("Security Ended");

                SecurityList.add(Security_map);
                pet_put.setSecurity(SecurityList);





















//            }

            paths_rs.close();//operation

            System.out.println("Paths ended");

            //components



            System.out.println(io.swagger.util.Json.pretty(petstore));

        }catch(Exception e){
            e.printStackTrace();
        }finally{
//            try{
//                if(stmt!=null)
//                    con.close();
//            }catch(SQLException ignored){
//            }
//            try{
//                if(con!=null)
//                    con.close();
//            }catch(SQLException se){
//                se.printStackTrace();
//            }
            try{
                con.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("Connecction closed!");






    }
}
