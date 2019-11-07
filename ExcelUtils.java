package com.mab.test.framework.helpers.utils;
/**
 * Created by Mitchells & Butlers plc.
 * Author: Jabez James
 * Date: 12/2/18
 * An utility to read testdata from spreadsheets
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;

/*
 * ExcelUtils class provides methods to read data from the Excel Spread Sheets
 */
public class ExcelUtils {

	static String fileName;
	static String sheetName;
	// All the functions with in the class are overloaded with an optional sheetname parameter
	// Constructors that initialises the class with the excel file path and the spread sheet names

	public ExcelUtils( String fName )
	{
		fileName = fName;
	}
	
	public ExcelUtils( String fName,String SName )
	{
		
		fileName = fName;
		sheetName = SName;

	}
	
	// Returns the total number of rows (datasets) within the spreadsheet
	
	public int rowCount(String SName) throws Exception 
	{
		
		 File src=new File(fileName);
		 FileInputStream fis=new FileInputStream(src);
		 XSSFWorkbook wb = new XSSFWorkbook(fis);
		  XSSFSheet sh1= wb.getSheet(SName);
		  int ctRows = sh1.getLastRowNum();
		  wb.close();
		  if(fis != null) fis.close();
		 return ctRows+1;
		
	}
	
	public int rowCount() throws Exception 
	{
		
		String SName;
		int ctRows;
		XSSFSheet sh1;
		 File src=new File(fileName);
		 FileInputStream fis=new FileInputStream(src);
		 XSSFWorkbook wb = new XSSFWorkbook(fis);
		  if (ExcelUtils.sheetName != null)
		  {
			  SName = ExcelUtils.sheetName;
			sh1= wb.getSheet(SName);

		  } else
		  {
			  
			  sh1= wb.getSheetAt(0);
		  }
		  ctRows = sh1.getLastRowNum();
			  
		 wb.close();
		 if(fis != null) fis.close();
		return ctRows+1;
		
	}
	
	// Returns the total number of columns within the spreadsheet
	
	public int colCount(String SName) throws Exception 
	{
		
		 File src=new File(fileName);
		 FileInputStream fis=new FileInputStream(src);
		 XSSFWorkbook wb = new XSSFWorkbook(fis);
		  XSSFSheet sh1= wb.getSheet(SName);
		  int ctRows = sh1.getLastRowNum();
		  XSSFRow row = sh1.getRow(ctRows);
		  int ctCols = row.getLastCellNum();
		  wb.close();
		  if( fis != null) fis.close();
		 return ctCols;
		
	}
	
	public int colCount() throws Exception 
	{
		
		String SName = null;
		int ctRows,ctCols;
		XSSFSheet sh1;
		
		 File src=new File(fileName);
		 FileInputStream fis=new FileInputStream(src);
		 XSSFWorkbook wb = new XSSFWorkbook(fis);
		 if (ExcelUtils.sheetName != null)
		  {
			  SName = ExcelUtils.sheetName;
		  sh1= wb.getSheet(SName);
		
		  }else
		  {
			  
			  sh1= wb.getSheetAt(0);
			  
		  }
		 ctRows = sh1.getLastRowNum();
		  XSSFRow row = sh1.getRow(ctRows);
		  ctCols = row.getLastCellNum();
		 
		  wb.close();
		  if(fis != null) fis.close();
		 return ctCols;
		
	}
	
	// Read the data within Excel spreadsheet and return a two dimensional array 
	
	@SuppressWarnings("deprecation")
	public String[][] readExcelContents(String SName) throws Exception 
	{
		 File src=new File(fileName);
		 FileInputStream fis=new FileInputStream(src);
		 XSSFWorkbook wb = new XSSFWorkbook(fis);
		  XSSFSheet sh1= wb.getSheet(SName);
		  int ctRows = sh1.getLastRowNum();
		  XSSFRow row = sh1.getRow(ctRows);
		  int ctCols = row.getLastCellNum();
		  int r = 0,i = 0;
			  
		String[][] content = new String[ctRows+1][ctCols] ;
  	 
  
		  for ( r = 0 ; r <= ctRows; r++)
		  {
			  for ( i = 0 ; i < ctCols; i++)
			  {
				  XSSFCell cell = sh1.getRow(r).getCell(i);
				  cell.setCellType(Cell.CELL_TYPE_STRING); 
				  content[r][i] = sh1.getRow(r).getCell(i).getStringCellValue();
				 // System.out.println(content[r][i]);
				  
			  }

		  }
		  wb.close();
		  if(fis != null) fis.close();
		  return content;
	}
	
	public String[][] readExcelContents() throws Exception 
	{
		XSSFSheet sh1;
		String SName = null;
		int ctRows,ctCols;
		 File src=new File(fileName);
		 FileInputStream fis=new FileInputStream(src);
		 XSSFWorkbook wb = new XSSFWorkbook(fis);
		 if (ExcelUtils.sheetName != null)
		{
			  SName = ExcelUtils.sheetName;
			  sh1= wb.getSheet(SName);
		}
		 else
		  {
			  
			  sh1= wb.getSheetAt(0);
			  
		  }
		  ctRows = sh1.getLastRowNum();
		  XSSFRow row = sh1.getRow(ctRows);
		  ctCols = row.getLastCellNum();
		  int r = 0,i = 0;
			  
		String[][] content = new String[ctRows+1][ctCols] ;
  	 
  
		  for ( r = 0 ; r <= ctRows; r++)
		  {
			  for ( i = 0 ; i < ctCols; i++)
			  {
				  XSSFCell cell = sh1.getRow(r).getCell(i);
				 cell.setCellType(Cell.CELL_TYPE_STRING); 
				  content[r][i] = sh1.getRow(r).getCell(i).getStringCellValue();
				 // System.out.println(content[r][i]);
				  
			  }

		  }
		  wb.close();
		  if(fis != null) fis.close();
		  return content;
	}
	
	
	

	// Read and returns a specific row of data within Excel spreadsheet 
	
	public HashMap<String,String> readRow(String SName,int rowNum) throws Exception 
	{
		
		HashMap<String,String> record = new HashMap<String, String>();
		int i = 0, j = 0;
		 i = rowNum ;
		 String[][] output = null;
		 
			ExcelUtils EU = new ExcelUtils(fileName);
			int rc = EU.rowCount(SName);
			int cc = EU.colCount(SName);
 		   output = new String[rc][cc];
			 output = EU.readExcelContents(SName);
			 
			//System.out.println("output.length = " + output.length);
			for (  j = 0 ; j < output[i].length; j++)
			{		
				record.put(output[0][j], output[i][j]);
			}
		
		return record;
		
	}
	
	public HashMap<String,String> readRow(int rowNum) throws Exception 
	{
		String SName = null;
		HashMap<String,String> record = new HashMap<String, String>();
		int i = 0, j = 0,rc = 0,cc = 0;
		 i = rowNum ;
		 String[][] output = null;
		 
			ExcelUtils EU = new ExcelUtils(fileName);
			 if (ExcelUtils.sheetName != null)
				{
					  SName = ExcelUtils.sheetName;
						rc = EU.rowCount(SName);
						cc = EU.colCount(SName);
				} else
				{
					
					rc = EU.rowCount();
					cc = EU.colCount();
				}
	
 		   output = new String[rc][cc];
 			 if (ExcelUtils.sheetName != null)
				{
 				    SName = ExcelUtils.sheetName;
 				 	output = EU.readExcelContents(SName);
				}else
				{
					output = EU.readExcelContents();
				}

			//System.out.println("output.length = " + output.length);
			for (  j = 0 ; j < output[i].length; j++)
			{		
				record.put(output[0][j], output[i][j]);
			}
		
		return record;
		}
	
	// Read and returns a specific row of data ( Corresponding to a column name and its unique value )within Excel spreadsheet 
	
	public HashMap<String,String> readRecord(String SName,String key,String value) throws Exception 
	{
		
		HashMap<String,String> record = new HashMap<String, String>();
		int i = 0, j = 0, rowNum;
		
		 String[][] output = null;
		 
			ExcelUtils EU = new ExcelUtils(fileName);
			int rc = EU.rowCount(SName);
			int cc = EU.colCount(SName);
			 int recNum = 0;
			 
			//System.out.println("output.length = " + output.length);
				for ( i = 1 ; i < rc; i++)
				{	
		      
					record = EU.readRow(SName,i); 
				 
					
			 // check set values contentEquals
					if (record.get(key).contentEquals(value) )
					{
						recNum = i;
						break;
					}
				}
		
			return record;
		
	}
	
	public HashMap<String,String> readRecord(String key,String value) throws Exception 
	{
		String SName = null;
		HashMap<String,String> record = new HashMap<String, String>();
		int i = 0, j = 0,rc = 0,cc = 0,recNum, rowNum;
		
		 String[][] output = null;
		 
			ExcelUtils EU = new ExcelUtils(fileName);
			if (ExcelUtils.sheetName != null)
			{
				  SName = ExcelUtils.sheetName;
					rc = EU.rowCount(SName);
					cc = EU.colCount(SName);
			} else
			{
			 rc = EU.rowCount();
			 cc = EU.colCount();
			}
			recNum = 0;
			 
			//System.out.println("output.length = " + output.length);
				for ( i = 1 ; i < rc; i++)
				{	
		      
					record = EU.readRow(i); 
				 
					
			 // check set values contentEquals
					if (record.get(key).contentEquals(value) )
					{
						recNum = i;
						break;
					}
				}
		
			return record;
		
	}

	 public void UpdateCellValue(int rnum,int cnum,String value)throws Exception
	 {
		 File src=new File(fileName);
		 FileInputStream fis = new FileInputStream(src);
		 XSSFWorkbook wb = new XSSFWorkbook(fis);
//Access the worksheet, so that we can update / modify it.
		 XSSFSheet sh = wb.getSheetAt(0);
		  sh.getRow(rnum).getCell(cnum).setCellValue(value);
		 XSSFFormulaEvaluator.evaluateAllFormulaCells(wb);
		 fis.close();
		 FileOutputStream fos = new FileOutputStream(src);
		 XSSFFormulaEvaluator.evaluateAllFormulaCells(wb);
		 wb.write(fos);
		 fos.close();
	 }
	
	// Creates an excel spreadsheet and write the header and and a single record passed into as arrays

	/*
	public synchronized void WriteSheetContents( String sName, String[] headers,String[] values) throws Exception 
	{
		File src=new File(fileName);
		
		FileOutputStream fos = new FileOutputStream(src);
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sName);
		
	    CellStyle style = workbook.createCellStyle();
	    style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		style.setFont(font);

		// Create headers
	
		XSSFRow row = sheet.createRow(0);
        for ( int i = 0 ; i < headers.length; i++)
		  {
		XSSFCell cell = row.createCell(i);
	
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellStyle(style);
        cell.setCellValue(headers[i]);
        
		  }
        
        // Create Contents
        XSSFRow row1 = sheet.createRow(1);
        for ( int i = 0 ; i < values.length; i++)
		  {
		XSSFCell cell = row1.createCell(i);
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(values[i]);
		  }
        
    	workbook.write(fos);
    	workbook.close();
		if(fos != null) fos.close();
		
	}
	
	*/
	// Write the data into excel spreadsheet passed into by a two dimensional array 
	
/*	
public synchronized void WriteSheetContents( String sName, String[][] contents) throws Exception 
	{
		File src=new File(fileName);
		
		FileOutputStream fos = new FileOutputStream(src);
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sName);
		
		// Create headers + contents
	for ( int r = 0 ; r < contents.length; r++)
	{
		XSSFRow row = sheet.createRow(r);
        for ( int i = 0 ; i < contents[r].length; i++)
		  {
		XSSFCell cell = row.createCell(i);
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(contents[r][i]);
		  }
	}
        

        
    	workbook.write(fos);
    	workbook.close();
		if(fos != null) fos.close();
		
	}
		
	*/
//Sample usage of the ExcelUtils 
	
	
	public static String[][] readExcelValues(String path) throws Exception 
	{
		ExcelUtils EU = new ExcelUtils(path);
		int rc = EU.rowCount();
		int cc = EU.colCount();
		String[][] cont =  EU.readExcelContents();
		
		String[][] out = new String[rc-1][cc-1] ;
		
		
		  for ( int r = 1 ; r < rc; r++)
		  {
			  for ( int i = 1 ; i < cc; i++)
			  {
				 out[r-1][i-1] = cont[r][i];
			  }

		  }
		  
		  for ( int r = 0; r < rc-1; r++)
		  {
			  for ( int i = 0 ; i < cc-1; i++)
			  {

				  System.out.println(out[r][i]);
			  }

		  }
		  return out;
	}

@SuppressWarnings("null")
public static void main(String []args) throws Exception
	
	{

	//String[][] c = readExcelValues("C:\\Automation\\B2BHub.xlsx");
	// HashMap definitions for reading records from spreadsheets
    HashMap<String,String> record1 = new HashMap<String, String>();
    HashMap<String,String> record2 = new HashMap<String, String>();
    
    
    // Creation of an object of class ExcelUtils
	 ExcelUtils EU = new ExcelUtils("C:\\AutomationFramework\\JavaSeleniumFramework\\src\\test\\resources\\testdesign\\MillerCarter.xlsx");

	// Calculates total number of rows
	  int rc = EU.rowCount();
	// Calculates total number of columns. 
	  int cc = EU.colCount();

		System.out.println("Total Number of the rows " + rc );
		System.out.println("Total Number of the columns" + cc );
      //int recNum = 0;
     /* String key;
      String value;
      	key = "SCENARIO";
      value = "1";
      
    //  Returns a row of record selected by its column name & its value ( key and value) combination.
      record1 = EU.readRecord(key,value);
    // Returns a row within spread sheet by its sequence –number.
     // record2 = EU.readRow(recNum);
		for (int i = 1; i < EU.rowCount(); i++) {
			EU.UpdateCellValue(i, 3,"Changed");
		}

	 
	  System.out.println(record1.get("DAY"));
	  
	  Set keys = record1.keySet();   // It will return you all the keys in Map in the form of the Set

		System.out.println("Total Number of the rows " + rc );
		Set<Entry<String, String>> set=record1.entrySet();
		  System.out.println("Set values: " + set);
		  System.out.println(set.size());
		  
	  for (Iterator i = keys.iterator(); i.hasNext();) 
	  {
		  	String key1 = (String) i.next();
	        System.out.println(record1.get(key1)); // Here is an Individual Record in your HashMap
	  }
			  */
			
		
	} 



}