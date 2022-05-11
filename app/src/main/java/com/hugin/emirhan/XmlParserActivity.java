package com.hugin.emirhan;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class XmlParserActivity extends AppCompatActivity {

    String path = null;
    String TAG = "XmlParserActivity";
    Button buttonXmlSelect;
    TextView textViewXmlParser;

    int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xml_parser);
        initThis();
        initClickableItems();

    }
    private void initThis(){
        buttonXmlSelect = findViewById(R.id.button_xml_parser_selectXml);
        textViewXmlParser= findViewById(R.id.textView_xml_parser);
    }

    private void initClickableItems(){
        buttonXmlSelect.setOnClickListener(view -> showFileChooser());
    }

    private static final int FILE_SELECT_CODE = 0;

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_SELECT_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                Log.d(TAG, "File Uri: " + uri.toString());

                path = getPath(this, uri);
                Log.d(TAG, "File Path: " + path);
                parseXmlAndSetData();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String getFileName(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri,null,null,null,null);
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return  cursor.getString(nameIndex);
    }


    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                if(true){
                    final String docId = DocumentsContract.getDocumentId(uri);
                    Log.d("External Storage", docId);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageState() + "/" + split[1];
                    }
                }
                else{
                    String path = uri.getPath();
                    String new_path = path.replaceFirst("document", "storage");
                    String new_path2 = new_path.replaceFirst(":", "/");
                    return new_path2;
                }


            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                String dstPath = context.getCacheDir().getAbsolutePath() + File.separator + getFileName(context,uri);

                if (true) {
                    Log.d("TAG", "copy file success: " + dstPath);
                    return dstPath;

                } else {
                    Log.d("TAG", "copy file fail!");
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
    @SuppressLint("SetTextI18n")
    private void parseXmlAndSetData(){
        textViewXmlParser.setText("Z No"+"\t"+"FişNo"+"\t"+"Fiş Tarihi"+"\t"+ "Toplam"+"\t"+"Kdv"+"\t"+"["+"\t"+"Oran "
                +"\t"+ "Tutar "+"\t"+ "Kdv;"+"\t"+"Oran"+"\t"+"Tutar"+"\t"+"Kdv;"+"\t"+"Oran"+"\t"+"Tutar"
                +"\t"+"Kdv;"+"\t"+ "Oran"+"\t"+  "Tutar"+"\t"+  " Kdv;"+"\t"+ "]");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(path));
            doc.getDocumentElement().normalize();

            System.out.println("Root Element :" + doc.getDocumentElement().getNodeName());
            System.out.println("------");

            // get <staff>
            NodeList list = doc.getElementsByTagName("Receipt");
            NodeList list2 = doc.getElementsByTagName("VatTotal");
            for (int temp = 0; temp < list.getLength(); temp++) {

                Node node = list.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;

                    // get staff's attribute
                    String id = element.getAttribute("Receipt");


                    // get text
                    String ZNo = element.getElementsByTagName("ZNo").item(0).getTextContent();
                    String ReceiptNo = element.getElementsByTagName("ReceiptNo").item(0).getTextContent();
                    String ReceiptDate = element.getElementsByTagName("ReceiptDate").item(0).getTextContent();
                    String Total = element.getElementsByTagName("Total").item(0).getTextContent();
                    String Vat = element.getElementsByTagName("Vat").item(0).getTextContent();
                    textViewXmlParser.setText(textViewXmlParser.getText()+"\n"+ZNo+"  "+ReceiptNo+"  "+ReceiptDate+"  "+Vat+"  "+Total+"  ");


                    System.out.println("Current Element :" + node.getNodeName());
                    System.out.println("ZNo : " + ZNo);
                    System.out.println("ReceiptNo : " + ReceiptNo);
                    System.out.println("ReceiptDate : " + ReceiptDate);
                    System.out.println("Vat : " + Vat);
                    System.out.println("Total : " + Total);




                    for (int temp2 = counter; temp2 < list2.getLength(); temp2++) {

                        Node node2 = list2.item(temp2);

                        if (node2.getNodeType() == Node.ELEMENT_NODE) {

                            Element element2 = (Element) node;


                            for(int i=0 ; i<4 ; i++){
                                String VatRate = element2.getElementsByTagName("VatRate").item(i).getTextContent();
                                String SaleAmount = element2.getElementsByTagName("SaleAmount").item(i).getTextContent();
                                String VatAmount = element2.getElementsByTagName("VatAmount").item(i).getTextContent();

                                textViewXmlParser.setText(textViewXmlParser.getText()+VatRate+"  "+SaleAmount+"  "+VatAmount+"  ");

                                System.out.println("VatRate : " + VatRate);
                                System.out.println("SaleAmount : " + SaleAmount);
                                System.out.println("VatAmount : " + VatAmount);

                                counter=temp2;
                            }

                        }
                        break;
                    }

                }
                textViewXmlParser.setText(textViewXmlParser.getText()+"\n");
            }

            openDirectory();

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }


    }


    private void openDirectory(){

        try {
            String rootPath = this.getExternalFilesDir(null).getAbsolutePath() + "/Sales/";
            System.out.println(rootPath);
            File root = new File(rootPath);
            if (!root.exists()) {
                root.mkdirs();
            }


            File salesFile = new File(rootPath+ "Sales.txt");

            if (!salesFile.exists()) {
                salesFile.createNewFile();
            }

            FileWriter writer = new FileWriter(salesFile,true);
            writer.append(textViewXmlParser.getText());
            writer.append(System.lineSeparator());
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}