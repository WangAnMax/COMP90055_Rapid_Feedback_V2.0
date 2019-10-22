package util;

import android.util.Log;

import dbclass.Criteria;

import dbclass.ShortText;
import dbclass.StudentInfo;
import dbclass.SubSection;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class ExcelParser {

    public ArrayList<Criteria> readXlsCriteria(String path) {
        ArrayList<Criteria> customisedCriteriaList = new ArrayList<>();
        try {
            // Creating Input Stream
            InputStream myInput;

            File file = new File(path);

            //  Don't forget to Change to your assets folder excel sheet
            myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells. **/
            Iterator<Row> rowIter = mySheet.rowIterator();

            ArrayList<CustomisedCriteria> criteriaList = new ArrayList<>();

            // Skip the first line of the table
            rowIter.next();

            while (rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator<Cell> cellIter = myRow.cellIterator();

                CustomisedCriteria newCriteria = new CustomisedCriteria();

                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    if (myCell.getColumnIndex() == 0) {
                        newCriteria.setCriteria(myCell.toString().trim());
                    }

                    if (myCell.getColumnIndex() == 1) {
                        newCriteria.setSubSection(myCell.toString().trim());
                    }

                    if (myCell.getColumnIndex() == 2) {
                        newCriteria.setShortText(myCell.toString().trim());
                    }

                    if (myCell.getColumnIndex() == 3) {
                        newCriteria.setLongText(myCell.toString().trim());
                    }

                    if (myCell.getColumnIndex() == 4) {
                        newCriteria.setGrade(Double.valueOf(myCell.toString().trim()).intValue());
                    }
                }
                Log.d("EEEE", "Cell Value: " + newCriteria.toString() + " Index :" + myRow.getRowNum());
                criteriaList.add(newCriteria);
            }
            Log.d("EEEE", criteriaList.toString());
            customisedCriteriaList = generateCriteriaList(criteriaList);
            Log.d("EEEE", customisedCriteriaList.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customisedCriteriaList;
    }

    public ArrayList<Criteria> generateCriteriaList(ArrayList<CustomisedCriteria> criteriaList) {
        Log.d("EEEE", "generate criteria list.");
        ArrayList<Criteria> customisedCriteriaList = new ArrayList<>();
        for(int i = 0; i < criteriaList.size(); i++) {
            Log.d("EEEE", "criteria size: " + criteriaList.size());
            Log.d("EEEE", "criteria index: " + i);
            String newCriteriaName = criteriaList.get(i).getCriteria().trim();
            String newSubsectionName = criteriaList.get(i).getSubSection().trim();
            String newShortText = criteriaList.get(i).getShortText().trim();
            String newLongText = criteriaList.get(i).getLongText().trim();
            int newGrade = criteriaList.get(i).getGrade();
            if(!(newCriteriaName.equals("") || newSubsectionName.equals("") || newShortText.equals("") || newLongText.equals(""))) {
//                Log.d("EEEE", newCriteriaName);
                int criteriaIndex = checkCriteriaName(newCriteriaName, customisedCriteriaList);
                if(criteriaIndex == -1) {
                    Criteria customisedCriteria = new Criteria();
                    customisedCriteria.setName(newCriteriaName);

                    ArrayList<SubSection> customisedSubSectionList = new ArrayList<>();
                    SubSection customisedSubsection = new SubSection();
                    customisedSubsection.setName(newSubsectionName);

                    ArrayList<ShortText> customisedShortTextList = new ArrayList<>();
                    ShortText customisedShortText = new ShortText();
                    customisedShortText.setName(newShortText);

                    ArrayList<String> customisedLongTextList = new ArrayList<>();
                    customisedLongTextList.add(newLongText);

                    customisedShortText.setLongtext(customisedLongTextList);
                    customisedShortText.setGrade(newGrade);
                    customisedShortTextList.add(customisedShortText);

                    customisedSubsection.setShortTextList(customisedShortTextList);
                    customisedSubSectionList.add(customisedSubsection);

                    customisedCriteria.setSubsectionList(customisedSubSectionList);
                    customisedCriteriaList.add(customisedCriteria);
                } else {
//                    Log.d("EEEE", newSubsectionName);
                    ArrayList<SubSection> customisedSubSectionList = customisedCriteriaList.get(criteriaIndex).getSubsectionList();
                    int subsectionIndex = checkSubSectionName(newSubsectionName, customisedSubSectionList);
                    if (subsectionIndex == -1) {
                        SubSection customisedSubsection = new SubSection();
                        customisedSubsection.setName(newSubsectionName);

                        ArrayList<ShortText> customisedShortTextList = new ArrayList<>();
                        ShortText customisedShortText = new ShortText();
                        customisedShortText.setName(newShortText);
                        customisedShortText.setGrade(newGrade);

                        ArrayList<String> customisedLongTextList = new ArrayList<>();
                        customisedLongTextList.add(newLongText);

                        customisedShortText.setLongtext(customisedLongTextList);
                        customisedShortTextList.add(customisedShortText);

                        customisedSubsection.setShortTextList(customisedShortTextList);
                        customisedSubSectionList.add(customisedSubsection);
                    } else {
//                        Log.d("EEEE", newShortText);
                        ArrayList<ShortText> customisedShortTextList = customisedSubSectionList.get(subsectionIndex).getShortTextList();
                        int shortTextIndex = checkShortText(newShortText, newGrade, customisedShortTextList);
                        Log.d("EEEE", newGrade + "");
                        if(shortTextIndex == -1) {
                            ShortText customisedShortText = new ShortText();
                            customisedShortText.setName(newShortText);
                            customisedShortText.setGrade(newGrade);

                            ArrayList<String> customisedLongTextList = new ArrayList<>();
                            customisedLongTextList.add(newLongText);

                            customisedShortText.setLongtext(customisedLongTextList);
                            customisedShortTextList.add(customisedShortText);
                        } else {
//                            Log.d("EEEE", newLongText);
                            ArrayList<String> customisedLongTextList = customisedShortTextList.get(shortTextIndex).getLongtext();
                            int longTextIndex = checkLongText(newLongText, customisedLongTextList);
                            if(longTextIndex == -1) {
                                customisedLongTextList.add(newLongText);
                            }
                        }
                    }
                }
            }
        }
        display(customisedCriteriaList);
        return customisedCriteriaList;
    }

    public void display(ArrayList<Criteria> customisedCriteriaList) {
        for(int i = 0; i < customisedCriteriaList.size(); i++) {
            Log.d("EEEE", "criteria: " + customisedCriteriaList.get(i).getName());
            for(int a = 0; a < customisedCriteriaList.get(i).getSubsectionList().size(); a++) {
                Log.d("EEEE", "subsection: " + customisedCriteriaList.get(i).getSubsectionList().get(a).getName());
                for(int b = 0; b < customisedCriteriaList.get(i).getSubsectionList().get(a).getShortTextList().size(); b++) {
                    Log.d("EEEE", "shorttext: " + customisedCriteriaList.get(i).getSubsectionList().get(a).getShortTextList().get(b).getName());
                    Log.d("EEEE", "shorttext grade: " + customisedCriteriaList.get(i).getSubsectionList().get(a).getShortTextList().get(b).getGrade());
                    for(int c = 0; c < customisedCriteriaList.get(i).getSubsectionList().get(a).getShortTextList().get(b).getLongtext().size(); c++) {
                        Log.d("EEEE", "longtext: " + customisedCriteriaList.get(i).getSubsectionList().get(a).getShortTextList().get(b).getLongtext().get(c));
                    }
                }
            }
        }
    }

    public int checkCriteriaName(String newCriteriaName, ArrayList<Criteria> customisedCriteriaList) {
        for(int i = 0; i < customisedCriteriaList.size(); i++) {
//            Log.d("EEEE", "size:  " + customisedCriteriaList.size());
            String oldCriteriaName = customisedCriteriaList.get(i).getName();
//            Log.d("EEEE", "old  " + oldCriteriaName);
//            Log.d("EEEE", "new  " + newCriteriaName);
            if(oldCriteriaName.equals(newCriteriaName)) {
                return i;
            }
        }
        return -1;
    }

    public int checkSubSectionName(String newSubsectionName, ArrayList<SubSection> customisedSubSectionList) {
        for(int i = 0; i < customisedSubSectionList.size(); i++) {
            String oldSubsectionName = customisedSubSectionList.get(i).getName();
            if(oldSubsectionName.equals(newSubsectionName)) {
                return i;
            }
        }
        return -1;
    }

    public int checkShortText(String newShortText, int newGrade, ArrayList<ShortText> customisedShortTextList) {
        for(int i = 0; i < customisedShortTextList.size(); i++) {
            String oldShortText = customisedShortTextList.get(i).getName();
            int oldGrade = customisedShortTextList.get(i).getGrade();
            if(oldShortText.equals(newShortText) && oldGrade == newGrade) {
                return i;
            }
        }
        return -1;
    }

    public int checkLongText(String newLongText, ArrayList<String> customisedLongTextList) {
        for(int i = 0; i < customisedLongTextList.size(); i++) {
            String oldLongText = customisedLongTextList.get(i);
            if(oldLongText.equals(newLongText)) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<Criteria> readXlsxCriteria(String path) {
        ArrayList<Criteria> customisedCriteriaList = new ArrayList<>();
        try {
            // Creating Input Stream
            InputStream myInput;

            File file = new File(path);

            //  Don't forget to Change to your assets folder excel sheet
            myInput = new FileInputStream(file);

            XSSFWorkbook workbook = new XSSFWorkbook(myInput);
            XSSFSheet mySheet = workbook.getSheetAt(0);


            /** We now need something to iterate through the cells. **/
            Iterator<Row> rowIter = mySheet.rowIterator();

            ArrayList<CustomisedCriteria> criteriaList = new ArrayList<>();

            // Skip the first row of the table
            rowIter.next();

            while (rowIter.hasNext()) {
                Row myRow = (Row) rowIter.next();
                Iterator<Cell> cellIter = myRow.cellIterator();

                CustomisedCriteria newCriteria = new CustomisedCriteria();

                while (cellIter.hasNext()) {
                    XSSFCell myCell = (XSSFCell) cellIter.next();
                    if (myCell.getColumnIndex() == 0) {
                        newCriteria.setCriteria(myCell.toString().trim());
                    }

                    if (myCell.getColumnIndex() == 1) {
                        newCriteria.setSubSection(myCell.toString().trim());
                    }

                    if (myCell.getColumnIndex() == 2) {
                        newCriteria.setShortText(myCell.toString().trim());
                    }

                    if (myCell.getColumnIndex() == 3) {
                        newCriteria.setLongText(myCell.toString().trim());
                    }

                    if (myCell.getColumnIndex() == 4) {
                        newCriteria.setGrade(Double.valueOf(myCell.toString().trim()).intValue());
                    }

                    Log.d("EEEE", "Cell Value: " + newCriteria.toString() + " Index :" + myCell.getColumnIndex());

                }
                criteriaList.add(newCriteria);
            }
            Log.d("EEEE", criteriaList.toString());
            customisedCriteriaList = generateCriteriaList(criteriaList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customisedCriteriaList;
    }

    public ArrayList<StudentInfo> readXlsStudents(String path) {
        ArrayList<StudentInfo> studentInfos = new ArrayList<>();

        try {
            // Creating Input Stream
            InputStream myInput;

            File file = new File(path);

            //  Don't forget to Change to your assets folder excel sheet
            myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells. **/
            Iterator<Row> rowIter = mySheet.rowIterator();

            // Skip the first line of the table
            rowIter.next();

            while (rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator<Cell> cellIter = myRow.cellIterator();

                StudentInfo newStudentInfo = new StudentInfo();
                boolean isValidStudentInfo = true;

                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    if (myCell.getColumnIndex() == 0) {
                        String studentID = myCell.toString().trim();
                        if (!studentID.equals("")) {
                            for (int i = 0; i < studentInfos.size(); i++) {
                                if (studentID.equals(studentInfos.get(i).getNumber())) {
                                    isValidStudentInfo = false;
                                }
                            }
                            newStudentInfo.setNumber(Double.valueOf(studentID).intValue() + "");
                        } else {
                            isValidStudentInfo = false;
                        }
                    }

                    if (myCell.getColumnIndex() == 1) {
                        String familyName = myCell.toString().trim();
                        if (!familyName.equals("")) {
                            newStudentInfo.setSurname(familyName);
                        } else {
                            isValidStudentInfo = false;
                        }
                    }

                    if (myCell.getColumnIndex() == 2) {
                        String middleName = myCell.toString().trim();
                        if (!middleName.equals("")) {
                            newStudentInfo.setMiddleName(middleName);
                        }
                    }

                    if (myCell.getColumnIndex() == 3) {
                        String givenName = myCell.toString().trim();
                        if (!givenName.equals("")) {
                            newStudentInfo.setFirstName(givenName);
                        } else {
                            isValidStudentInfo = false;
                        }
                    }

                    if (myCell.getColumnIndex() == 4) {
                        String groupNumber = myCell.toString().trim();
                        if (!groupNumber.equals("")) {
                            newStudentInfo.setGroup(Double.valueOf(groupNumber).intValue());
                        }
                    }

                    if (myCell.getColumnIndex() == 5) {
                        String email = myCell.toString().trim();
                        if (!email.equals("")) {
                            newStudentInfo.setEmail(email);
                        } else {
                            isValidStudentInfo = false;
                        }
                    }
                }

                Log.d("EEEE", "Cell Value: " + newStudentInfo.toString() + " Index :" + myRow.getRowNum());
                if (!((newStudentInfo.getNumber() == null) || (newStudentInfo.getFirstName() == null) || (newStudentInfo.getSurname() == null)
                        || (newStudentInfo.getEmail() == null))) {
                    Log.d("EEEE", "valid student info");
                    if (newStudentInfo.getMiddleName() == null) {
                        newStudentInfo.setMiddleName("");
                    }

                    studentInfos.add(newStudentInfo);
                }
            }
            Log.d("EEEE", studentInfos.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return studentInfos;
    }

    public ArrayList<StudentInfo> readXlsxStudents(String path) {
        ArrayList<StudentInfo> studentInfos = new ArrayList<>();

        try {
            // Creating Input Stream
            InputStream myInput;

            File file = new File(path);

            //  Don't forget to Change to your assets folder excel sheet
            myInput = new FileInputStream(file);

            XSSFWorkbook workbook = new XSSFWorkbook(myInput);
            XSSFSheet mySheet = workbook.getSheetAt(0);

            /** We now need something to iterate through the cells. **/
            Iterator<Row> rowIter = mySheet.rowIterator();

            // Skip the first row of the table
            rowIter.next();

            while (rowIter.hasNext()) {
                Row myRow = (Row) rowIter.next();
                Iterator<Cell> cellIter = myRow.cellIterator();

                StudentInfo newStudentInfo = new StudentInfo();
                boolean isValidStudentInfo = true;

                while (cellIter.hasNext()) {
                    XSSFCell myCell = (XSSFCell) cellIter.next();
                    if (myCell.getColumnIndex() == 0) {
                        String studentID = myCell.toString().trim();

                        if (!studentID.equals("")) {
                            for (int i = 0; i < studentInfos.size(); i++) {
                                if (studentID.equals(studentInfos.get(i).getNumber())) {
                                    isValidStudentInfo = false;
                                }
                            }
                            newStudentInfo.setNumber(Double.valueOf(studentID).intValue() + "");
                        } else {
                            isValidStudentInfo = false;
                        }
                    }

                    if (myCell.getColumnIndex() == 1) {
                        String familyName = myCell.toString().trim();
                        if (!familyName.equals("")) {
                            newStudentInfo.setSurname(familyName);
                        } else {
                            isValidStudentInfo = false;
                        }
                    }

                    if (myCell.getColumnIndex() == 2) {
                        String middleName = myCell.toString().trim();
                        if (!middleName.equals("")) {
                            newStudentInfo.setMiddleName(middleName);
                        } else {
                            newStudentInfo.setMiddleName("");
                        }
                    }

                    if (myCell.getColumnIndex() == 3) {
                        String givenName = myCell.toString().trim();
                        if (!givenName.equals("")) {
                            newStudentInfo.setFirstName(givenName);
                        } else {
                            isValidStudentInfo = false;
                        }
                    }

                    if (myCell.getColumnIndex() == 4) {
                        String groupNumber = myCell.toString().trim();
                        if (!groupNumber.equals("")) {
                            newStudentInfo.setGroup(Double.valueOf(myCell.toString().trim()).intValue());
                        }
                    }

                    if (myCell.getColumnIndex() == 5) {
                        String email = myCell.toString().trim();
                        if (!email.equals("")) {
                            newStudentInfo.setEmail(email);
                        } else {
                            isValidStudentInfo = false;
                        }
                    }
                }

                Log.d("EEEE", "Cell Value: " + newStudentInfo.toString() + " Index :" + myRow.getRowNum());
                if (!((newStudentInfo.getNumber() == null) || (newStudentInfo.getFirstName() == null) || (newStudentInfo.getSurname() == null)
                        || (newStudentInfo.getEmail() == null))) {
                    Log.d("EEEE", "valid student info");
                    if (newStudentInfo.getMiddleName() == null) {
                        newStudentInfo.setMiddleName("");
                    }

                    studentInfos.add(newStudentInfo);
                }
            }
            Log.d("EEEE", studentInfos.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return studentInfos;
    }
}
