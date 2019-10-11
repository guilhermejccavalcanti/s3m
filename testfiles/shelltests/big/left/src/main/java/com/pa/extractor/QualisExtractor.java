package com.pa.extractor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pa.database.impl.DatabaseFacade;
import com.pa.entity.Qualis;
import com.pa.entity.QualisData;
import com.pa.exception.InvalidPatternFileException;
import com.pa.util.EnumPublicationLocalType;

public class QualisExtractor {

	@SuppressWarnings("resource")
	public QualisData conferenceExtractor(String year, InputStream fileInputStream, String fileName) throws InvalidPatternFileException {
			
		QualisData resultQualisData = new QualisData(fileName, EnumPublicationLocalType.CONFERENCE, Integer.valueOf(year));
	
		try {
            XSSFWorkbook myWorkBook = new XSSFWorkbook (fileInputStream);
            XSSFSheet sheet = myWorkBook.getSheetAt(15);
			
			List<Qualis> qualis = this.extractQualisInformation(sheet, 2, 2, 4);
			resultQualisData.getQualis().addAll(qualis);
			
			if (resultQualisData.getQualis().isEmpty()) {
				throw new InvalidPatternFileException("Qualis file is invalid: there is no qualis itens");
			}
			else {
				resultQualisData = saveQualisData(resultQualisData);
			}
		} catch (Exception e) {
			throw new InvalidPatternFileException(e.getMessage());
		}
		
		return resultQualisData;
	}
	
	@SuppressWarnings("resource")
	public QualisData publicationExtractor(String year, InputStream fileInputStream, String fileName) throws InvalidPatternFileException {
		
		QualisData resultQualisData = new QualisData(fileName, EnumPublicationLocalType.PERIODIC, Integer.valueOf(year));
	
		try {
			HSSFWorkbook wb = new HSSFWorkbook(fileInputStream);
			HSSFSheet sheet = wb.getSheetAt(0);
			
			List<Qualis> qualis = this.extractQualisInformation(sheet, 1, 1, 2);
			resultQualisData.getQualis().addAll(qualis);
						
			if (resultQualisData.getQualis().isEmpty()) {
				throw new InvalidPatternFileException("Qualis file is invalid: there is no qualis itens");
			}
			else {
				resultQualisData = saveQualisData(resultQualisData);
			}
		} catch (Exception e) {
			throw new InvalidPatternFileException(e.getMessage());
		}
		
		return resultQualisData;
	}
	
	private QualisData saveQualisData(QualisData data) {
		QualisData dataResult;
		
		QualisData dataBaseQualisData = DatabaseFacade.getInstance().getQualisDataByTypeAndYear(data.getType(), data.getYear());;
		if (dataBaseQualisData == null) {
			dataResult = DatabaseFacade.getInstance().saveQualisData(data);
		}
		else {
			dataResult = dataBaseQualisData;
			
			dataResult.setFileName(data.getFileName());
			dataResult.setQualis(data.getQualis());
			
			DatabaseFacade.getInstance().updateQualisData(dataResult);
		}
		
		return dataResult;
	}
	
	private List<Qualis> extractQualisInformation(Sheet sheet, int initialLine, int columnName, int columnQualis){
		List<Qualis> listQualis = new ArrayList<Qualis>();
		
		int rows = sheet.getPhysicalNumberOfRows();

		Row row;
		Cell cellName;
		Cell cellQualis;

		for(int r = initialLine; r < rows; r++) {
			row = sheet.getRow(r);
			if(row != null) {
				Qualis qualis;
				String title = null;
				String qualisClassification = null;
				
				cellName = row.getCell(columnName);
				if (cellName != null) {
					title = cellName.getStringCellValue();
				}
				
				cellQualis = row.getCell(columnQualis);
				if (cellQualis != null) {
					qualisClassification = cellQualis.getStringCellValue();
				}
				
				if (title != null && qualisClassification != null) {
					qualis = new Qualis(title, qualisClassification);
					listQualis.add(qualis);
				}
			}
		}
		
		return listQualis;
	}
}
