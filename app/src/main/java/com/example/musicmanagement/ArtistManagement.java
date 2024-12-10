package com.example.musicmanagement;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.graphics.Canvas;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicmanagement.Artist;

public class ArtistManagement extends AppCompatActivity {
    // Khai báo các biến giao diện
    EditText edtmacasi;
    EditText edttencasi;
    EditText edtnhapthongtin;
    EditText edtsinhnam;
    Button btninsert, btndelete, btnupdate, btntimkiem, btnUpload;
    ListView lv;
    ArrayList<Artist> mylist;
    ArtistAdapter myadapter;
    SQLiteDatabase mydatabase;

    private static final int PICK_IMAGE = 1;
    ImageView imageView;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_management);
        // Liên kết các biến giao diện
        edtmacasi = findViewById(R.id.edtmacasi);
        edttencasi = findViewById(R.id.edttencasi);
        edtnhapthongtin = findViewById(R.id.edtnhapthongtin);
        edtsinhnam = findViewById(R.id.edtsinhnam);
        btninsert = findViewById(R.id.btninsert);
        btndelete = findViewById(R.id.btndelete);
        btnupdate = findViewById(R.id.btnupdate);
        btntimkiem = findViewById(R.id.btntimkiem);
        btnUpload = findViewById(R.id.btnUpload);
        lv = findViewById(R.id.lv);
        imageView = findViewById(R.id.imageView);

        //
        findViewById(R.id.btnRedirectHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Khởi tạo danh sách và adapter
        mylist = new ArrayList<>();
        myadapter = new ArtistAdapter(this, mylist);
        lv.setAdapter(myadapter);

        // Tạo cơ sở dữ liệu
        mydatabase = openOrCreateDatabase("musicmanagement.db", MODE_PRIVATE, null);

//         Tạo bảng để chứa dữ liệu
        try {
            String sql = "CREATE TABLE casi(macasi INTEGER PRIMARY KEY, tencasi TEXT, sinhnam INTEGER, image BLOB)";
            mydatabase.execSQL(sql);
        } catch (Exception e) {
            Log.e("error", "Table đã tồn tại");
        }
        // Hiển thị thông tin trong ListView ngay từ đầu
        displayData();

        // Thêm sự kiện click cho nút INSERT
        btninsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String macasi = edtmacasi.getText().toString();
                String tencasi = edttencasi.getText().toString();
                String sinhnamStr = edtsinhnam.getText().toString();
                Bitmap image = null;

                // Kiểm tra nếu có ảnh trong ImageView
                Drawable drawable = imageView.getDrawable();
                if (drawable != null) {
                    // Chuyển đổi Drawable sang Bitmap
                    image = drawableToBitmap(drawable);
                }

                // Kiểm tra thông tin đầu vào
                if (macasi.isEmpty() || tencasi.isEmpty() || sinhnamStr.isEmpty() || image == null) {
                    Toast.makeText(ArtistManagement.this, "Vui lòng nhập đầy đủ thông tin và chọn ảnh!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int sinhnam;
                try {
                    sinhnam = Integer.parseInt(sinhnamStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(ArtistManagement.this, "Vui lòng nhập năm sinh hợp lệ!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Thêm dữ liệu vào SQLite
                ContentValues myvalue = new ContentValues();
                myvalue.put("macasi", macasi);
                myvalue.put("tencasi", tencasi);
                myvalue.put("sinhnam", sinhnam);

                // Lưu ảnh vào cơ sở dữ liệu
                if (image != null) {
                    myvalue.put("image", getBitmapAsByteArray(image)); // Lưu ảnh dưới dạng BLOB
                }

                String msg;
                if (mydatabase.insert("casi", null, myvalue) == -1) {
                    msg = "Mã ca sĩ đã tồn tại, vui lòng nhập lại mã";
                } else {
                    msg = "Insert thành công";
                    mylist.add(new Artist(macasi, tencasi, sinhnam, image)); // Thêm dữ liệu vào ListView
                    myadapter.notifyDataSetChanged(); // Cập nhật lại ListView
                }
                Toast.makeText(ArtistManagement.this, msg, Toast.LENGTH_SHORT).show();

                // Làm mới các trường EditText và ImageView
                edtmacasi.setText("");
                edttencasi.setText("");
                edtsinhnam.setText("");
                imageView.setImageDrawable(null); // Hoặc imageView.setVisibility(View.GONE); nếu bạn muốn ẩn nó
            }
        });

        // Thêm sự kiện click cho nút DELETE
        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String macasi = edtmacasi.getText().toString();
                int n = mydatabase.delete("casi", "macasi=?", new String[]{macasi});
                String msg = n == 0 ? "Delete thất bại" : "Delete thành công";
                if (n > 0) {
                    displayData();
                    // clear
                    edtmacasi.setText("");
                    edttencasi.setText("");
                    edtsinhnam.setText("");
                    imageView.setImageDrawable(null);
                }
                Toast.makeText(ArtistManagement.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        // Thêm sự kiện click cho nút UPDATE
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String macasi = edtmacasi.getText().toString();
                String tencasi = edttencasi.getText().toString();
                String sinhnamStr = edtsinhnam.getText().toString();

                ContentValues myvalue = new ContentValues();
                myvalue.put("tencasi", tencasi);
                if (!sinhnamStr.isEmpty()) {
                    myvalue.put("sinhnam", Integer.parseInt(sinhnamStr));
                }

                int n = mydatabase.update("casi", myvalue, "macasi=?", new String[]{macasi});
                String msg = n == 0 ? "Update thất bại" : "Update thành công";
                if (n > 0) {
                    displayData();
                }
                Toast.makeText(ArtistManagement.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        // Thêm sự kiện click cho nút Tìm Kiếm
        btntimkiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mylist.clear();
                String searchInput = edtnhapthongtin.getText().toString().trim();

                Cursor c;
                if (!searchInput.isEmpty()) {
                    c = mydatabase.query("casi", null, "macasi=? OR tencasi=?", new String[]{searchInput, searchInput}, null, null, null);
                } else {
                    c = mydatabase.query("casi", null, null, null, null, null, null);
                }

                while (c.moveToNext()) {
                    String macasi = c.getString(0);
                    String tencasi = c.getString(1);
                    int sinhnam = c.getInt(2);
                    byte[] imageBytes = c.getBlob(3); // Lấy ảnh từ cơ sở dữ liệu

                    Bitmap image = null;
                    if (imageBytes != null) {
                        image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length); // Chuyển đổi byte[] thành Bitmap
                    }

                    mylist.add(new Artist(macasi, tencasi, sinhnam, image)); // Thêm dữ liệu vào danh sách
                }
                c.close();
                myadapter.notifyDataSetChanged();
            }
        });

        // Thêm sự kiện click cho các mục trong ListView
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist selectedItem = mylist.get(position);
                edtmacasi.setText(selectedItem.getMacasi());
                edttencasi.setText(selectedItem.getTencasi());
                edtsinhnam.setText(String.valueOf(selectedItem.getSinhnam()));
            }
        });

        // Thêm sự kiện click cho nút Upload
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh"), PICK_IMAGE);
            }
        });
    }

    // Hàm này sẽ được gọi khi ảnh được chọn
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try (InputStream inputStream = getContentResolver().openInputStream(imageUri)) {
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (imageView != null) {
                        imageView.setImageBitmap(bitmap); // Hiển thị ảnh trong ImageView
                    } else {
                        Log.e("ImageView Error", "ImageView is null");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Hàm chuyển đổi Drawable sang Bitmap
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    // Chuyển đổi Bitmap thành byte[]
    private byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    // Hàm hiển thị dữ liệu trong ListView
    private void displayData() {
        mylist.clear();
        Cursor c = mydatabase.query("casi", null, null, null, null, null, null);
        while (c.moveToNext()) {
            String macasi = c.getString(0);
            String tencasi = c.getString(1);
            int sinhnam = c.getInt(2);
            byte[] imageBytes = c.getBlob(3); // Lấy ảnh từ cơ sở dữ liệu

            Bitmap image = null;
            if (imageBytes != null) {
                image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length); // Chuyển đổi byte[] thành Bitmap
            }

            mylist.add(new Artist(macasi, tencasi, sinhnam, image)); // Thêm dữ liệu vào danh sách
        }
        c.close();
        myadapter.notifyDataSetChanged(); // Cập nhật ListView
    }
}
