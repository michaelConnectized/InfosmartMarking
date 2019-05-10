package com.example.user.markingactivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class LandmarkXLSXActivity extends AppCompatActivity {
    private ArrayList<String[]> lms;

    TextView tv_lm_xlsx_name;
    TextView tv_lm_xlsx_no;
    LinearLayout ll_landmarks_table;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark_xlsx);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        String fileExtension = intent.getStringExtra("fileExtension");
        if (fileExtension==null) {
            fileExtension = "";
        }

        tv_lm_xlsx_name = findViewById(R.id.tv_lm_xlsx_name);
        tv_lm_xlsx_no = findViewById(R.id.tv_lm_xlsx_no);
        ll_landmarks_table = findViewById(R.id.ll_landmarks_table);
        lms = new ArrayList<String[]>();

        getLandmarksFromXLSX(fileExtension);
        editLandmarkTable();
        tv_lm_xlsx_name.setText(ExcelPath.xlsx("lm"+fileExtension));
        tv_lm_xlsx_no.setText(String.valueOf(count));
    }

    public void getLandmarksFromXLSX(String fileExtension) {
        try {
            FileInputStream excelFile = new FileInputStream(new File(ExcelPath.xlsx(ExcelPath.FilterLandmark+fileExtension)));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();

            if (!iterator.hasNext()) {
                return;
            }
            count--;
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();

                String[] tmpString = new String[7];
                String tmpResult = "";

                if (currentRow.getCell(0, Row.CREATE_NULL_AS_BLANK).getCellType() != Cell.CELL_TYPE_BLANK) {
                    //get the data from cell
                    if (currentRow.getCell(0).getCellType() == XSSFCell.CELL_TYPE_STRING) {
                        tmpResult = currentRow.getCell(0).getStringCellValue();
                    } else if (currentRow.getCell(0).getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                        tmpResult = String.valueOf(currentRow.getCell(0).getNumericCellValue());
                    }
                }

                if (tmpResult.equals("")) {
                    //continue;
                }

                tmpString[0] = tmpResult;

                for (int i=1; i<7; i++) {
                    tmpResult = "";

                    if (currentRow.getCell(i, Row.CREATE_NULL_AS_BLANK).getCellType() != Cell.CELL_TYPE_BLANK) {
                        //get the data from cell
                        if (currentRow.getCell(i).getCellType() == XSSFCell.CELL_TYPE_STRING) {
                            tmpResult = currentRow.getCell(i).getStringCellValue();
                        } else if (currentRow.getCell(i).getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                            tmpResult = String.valueOf(currentRow.getCell(i).getNumericCellValue());
                        }
                    }

                    tmpString[i] = tmpResult;
                }

                lms.add(tmpString);
                count++;
            }
        } catch (FileNotFoundException e) {
            Log.d("xlsResult",e.toString());
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("xlsResult",e.toString());
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void editLandmarkTable() {
        ll_landmarks_table.removeAllViews();
        Boolean isEven = true;

        for (String[] lm: lms) {
            LinearLayout tmpLL = new LinearLayout(getApplicationContext());
            tmpLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            tmpLL.setOrientation(LinearLayout.HORIZONTAL);
            tmpLL.setWeightSum(7);
            LinearLayout.LayoutParams tmpParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            tmpParams.gravity = Gravity.CENTER;
            for (int j=0; j<7; j++) {
                TextView tmpTxtView = new TextView(getApplicationContext());
                tmpTxtView.setLayoutParams(tmpParams);
                tmpTxtView.setTextColor(Color.parseColor("#000000"));
                tmpTxtView.setGravity(Gravity.CENTER);
                tmpTxtView.setText(lm[j]);
                tmpLL.addView(tmpTxtView);
            }
            if (isEven) {
                tmpLL.setBackgroundColor( Color.parseColor("#e6e6e6"));
                isEven = false;
            } else {
                isEven = true;
            }
            ll_landmarks_table.addView(tmpLL);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
