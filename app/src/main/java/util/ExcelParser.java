/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package util;

import android.util.Log;

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

import newdbclass.Comment;
import newdbclass.Criterion;
import newdbclass.ExpandedComment;
import newdbclass.Field;
import newdbclass.Student;

public class ExcelParser {

    public ArrayList<Criterion> readXlsCriteria(String path) {
        ArrayList<Criterion> customisedCriteriaList = new ArrayList<>();
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

            ArrayList<CustomisedCriterion> criteriaList = new ArrayList<>();

            // Skip the first line of the table
            rowIter.next();

            while (rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator<Cell> cellIter = myRow.cellIterator();

                CustomisedCriterion newCriterion = new CustomisedCriterion();

                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    if (myCell.getColumnIndex() == 0) {
                        newCriterion.setCriterion(myCell.toString().trim());
                    }

                    if (myCell.getColumnIndex() == 1) {
                        newCriterion.setField(myCell.toString().trim());
                    }

                    if (myCell.getColumnIndex() == 2) {
                        newCriterion.setComment(myCell.toString().trim());
                    }

                    if (myCell.getColumnIndex() == 3) {
                        newCriterion.setExpandedComment(myCell.toString().trim());
                    }

                    if (myCell.getColumnIndex() == 4) {
                        newCriterion.setType(myCell.toString().trim());
                    }
                }
                criteriaList.add(newCriterion);
            }
            customisedCriteriaList = generateCriteriaList(criteriaList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customisedCriteriaList;
    }

    public ArrayList<Criterion> generateCriteriaList(ArrayList<CustomisedCriterion> criteriaList) {
        ArrayList<Criterion> customisedCriteriaList = new ArrayList<>();
        for (int i = 0; i < criteriaList.size(); i++) {
            String newCriterionName = criteriaList.get(i).getCriterion().trim();
            String newFieldName = criteriaList.get(i).getField().trim();
            String newComment = criteriaList.get(i).getComment().trim();
            String newExpandedComment = criteriaList.get(i).getExpandedComment().trim();
            String newType = criteriaList.get(i).getType();
            if (!(newCriterionName.equals("") || newFieldName.equals("") || newComment.equals("") || newExpandedComment.equals(""))) {
                int criteriaIndex = checkCriteriaName(newCriterionName, customisedCriteriaList);
                if (criteriaIndex == -1) {
                    Criterion customisedCriteria = new Criterion();
                    customisedCriteria.setName(newCriterionName);

                    ArrayList<Field> customisedSubSectionList = new ArrayList<>();
                    Field customisedSubsection = new Field();
                    customisedSubsection.setName(newFieldName);

                    ArrayList<Comment> customisedShortTextList = new ArrayList<>();
                    Comment customisedShortText = new Comment();
                    customisedShortText.setText(newComment);

                    ArrayList<ExpandedComment> customisedLongTextList = new ArrayList<>();
                    ExpandedComment customisedExComment = new ExpandedComment();
                    customisedExComment.setText(newExpandedComment);
                    customisedLongTextList.add(customisedExComment);

                    customisedShortText.setExpandedCommentList(customisedLongTextList);
                    customisedShortText.setType(newType);
                    customisedShortTextList.add(customisedShortText);

                    customisedSubsection.setCommentList(customisedShortTextList);
                    customisedSubSectionList.add(customisedSubsection);

                    customisedCriteria.setFieldList(customisedSubSectionList);
                    customisedCriteriaList.add(customisedCriteria);
                } else {
                    ArrayList<Field> customisedSubSectionList = customisedCriteriaList.get(criteriaIndex).getFieldList();
                    int subsectionIndex = checkSubSectionName(newFieldName, customisedSubSectionList);
                    if (subsectionIndex == -1) {
                        Field customisedSubsection = new Field();
                        customisedSubsection.setName(newFieldName);

                        ArrayList<Comment> customisedShortTextList = new ArrayList<>();
                        Comment customisedShortText = new Comment();
                        customisedShortText.setText(newComment);
                        customisedShortText.setType(newType);

                        ArrayList<ExpandedComment> customisedLongTextList = new ArrayList<>();
                        ExpandedComment customisedExComment = new ExpandedComment();
                        customisedExComment.setText(newExpandedComment);
                        customisedLongTextList.add(customisedExComment);

                        customisedShortText.setExpandedCommentList(customisedLongTextList);
                        customisedShortTextList.add(customisedShortText);

                        customisedSubsection.setCommentList(customisedShortTextList);
                        customisedSubSectionList.add(customisedSubsection);
                    } else {
                        ArrayList<Comment> customisedShortTextList = customisedSubSectionList.get(subsectionIndex).getCommentList();
                        int shortTextIndex = checkShortText(newComment, newType, customisedShortTextList);
                        if (shortTextIndex == -1) {
                            Comment customisedShortText = new Comment();
                            customisedShortText.setText(newComment);
                            customisedShortText.setType(newType);

                            ArrayList<ExpandedComment> customisedLongTextList = new ArrayList<>();
                            ExpandedComment customisedExComment = new ExpandedComment();
                            customisedExComment.setText(newExpandedComment);
                            customisedLongTextList.add(customisedExComment);

                            customisedShortText.setExpandedCommentList(customisedLongTextList);
                            customisedShortTextList.add(customisedShortText);
                        } else {
                            ArrayList<ExpandedComment> customisedLongTextList = customisedShortTextList.get(shortTextIndex).getExpandedCommentList();
                            int longTextIndex = checkLongText(newExpandedComment, customisedLongTextList);
                            if (longTextIndex == -1) {
                                ExpandedComment customisedExComment = new ExpandedComment();
                                customisedExComment.setText(newExpandedComment);
                                customisedLongTextList.add(customisedExComment);
                            }
                        }
                    }
                }
            }
        }
//        display(customisedCriteriaList);
        return customisedCriteriaList;
    }

    public void display(ArrayList<Criterion> customisedCriteriaList) {
        for (int i = 0; i < customisedCriteriaList.size(); i++) {
            Log.d("EEEE", "criteria: " + customisedCriteriaList.get(i).getName());
            for (int a = 0; a < customisedCriteriaList.get(i).getFieldList().size(); a++) {
                Log.d("EEEE", "subsection: " + customisedCriteriaList.get(i).getFieldList().get(a).getName());
                for (int b = 0; b < customisedCriteriaList.get(i).getFieldList().get(a).getCommentList().size(); b++) {
                    Log.d("EEEE", "shorttext: " + customisedCriteriaList.get(i).getFieldList().get(a).getCommentList().get(b).getText());
                    Log.d("EEEE", "shorttext grade: " + customisedCriteriaList.get(i).getFieldList().get(a).getCommentList().get(b).getType());
                    for (int c = 0; c < customisedCriteriaList.get(i).getFieldList().get(a).getCommentList().get(b).getExpandedCommentList().size(); c++) {
                        Log.d("EEEE", "longtext: " + customisedCriteriaList.get(i).getFieldList().get(a).getCommentList().get(b).getExpandedCommentList().get(c).getText());
                    }
                }
            }
        }
    }

    public int checkCriteriaName(String newCriteriaName, ArrayList<Criterion> customisedCriteriaList) {
        for (int i = 0; i < customisedCriteriaList.size(); i++) {
            String oldCriteriaName = customisedCriteriaList.get(i).getName();
            if (oldCriteriaName.equals(newCriteriaName)) {
                return i;
            }
        }
        return -1;
    }

    public int checkSubSectionName(String newSubsectionName, ArrayList<Field> customisedSubSectionList) {
        for (int i = 0; i < customisedSubSectionList.size(); i++) {
            String oldSubsectionName = customisedSubSectionList.get(i).getName();
            if (oldSubsectionName.equals(newSubsectionName)) {
                return i;
            }
        }
        return -1;
    }

    public int checkShortText(String newShortText, String newGrade, ArrayList<Comment> customisedShortTextList) {
        for (int i = 0; i < customisedShortTextList.size(); i++) {
            String oldShortText = customisedShortTextList.get(i).getText();
            String oldGrade = customisedShortTextList.get(i).getType();
            if (oldShortText.equals(newShortText) && oldGrade.equals(newGrade)) {
                return i;
            }
        }
        return -1;
    }

    public int checkLongText(String newLongText, ArrayList<ExpandedComment> customisedLongTextList) {
        for (int i = 0; i < customisedLongTextList.size(); i++) {
            String oldLongText = customisedLongTextList.get(i).getText();
            if (oldLongText.equals(newLongText)) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<Criterion> readXlsxCriteria(String path) {
        ArrayList<Criterion> customisedCriteriaList = new ArrayList<>();
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

            ArrayList<CustomisedCriterion> criteriaList = new ArrayList<>();

            // Skip the first row of the table
            rowIter.next();

            while (rowIter.hasNext()) {
                Row myRow = (Row) rowIter.next();
                Iterator<Cell> cellIter = myRow.cellIterator();

                CustomisedCriterion newCriteria = new CustomisedCriterion();

                while (cellIter.hasNext()) {
                    XSSFCell myCell = (XSSFCell) cellIter.next();
                    if (myCell.getColumnIndex() == 0) {
                        newCriteria.setCriterion(myCell.toString().trim());
                    }

                    if (myCell.getColumnIndex() == 1) {
                        newCriteria.setField(myCell.toString().trim());
                    }

                    if (myCell.getColumnIndex() == 2) {
                        newCriteria.setComment(myCell.toString().trim());
                    }

                    if (myCell.getColumnIndex() == 3) {
                        newCriteria.setExpandedComment(myCell.toString().trim());
                    }

                    if (myCell.getColumnIndex() == 4) {
                        newCriteria.setType(myCell.toString().trim());
                    }
                }
                criteriaList.add(newCriteria);
            }
            customisedCriteriaList = generateCriteriaList(criteriaList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customisedCriteriaList;
    }

    public ArrayList<Student> readXlsStudents(String path) {
        ArrayList<Student> students = new ArrayList<>();

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

                Student newStudent = new Student();
                boolean isValidStudentInfo = true;

                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    if (myCell.getColumnIndex() == 0) {
                        String studentID = myCell.toString().trim();
                        if (!studentID.equals("")) {
                            for (int i = 0; i < students.size(); i++) {
                                if (studentID.equals(students.get(i).getStudentNumber())) {
                                    isValidStudentInfo = false;
                                }
                            }
                            newStudent.setStudentNumber(Double.valueOf(studentID).intValue());
                        } else {
                            isValidStudentInfo = false;
                        }
                    }

                    if (myCell.getColumnIndex() == 1) {
                        String familyName = myCell.toString().trim();
                        if (!familyName.equals("")) {
                            newStudent.setLastName(familyName);
                        } else {
                            isValidStudentInfo = false;
                        }
                    }

                    if (myCell.getColumnIndex() == 2) {
                        String middleName = myCell.toString().trim();
                        if (!middleName.equals("")) {
                            newStudent.setMiddleName(middleName);
                        }
                    }

                    if (myCell.getColumnIndex() == 3) {
                        String givenName = myCell.toString().trim();
                        if (!givenName.equals("")) {
                            newStudent.setFirstName(givenName);
                        } else {
                            isValidStudentInfo = false;
                        }
                    }

                    if (myCell.getColumnIndex() == 4) {
                        String groupNumber = myCell.toString().trim();
                        if (!groupNumber.equals("")) {
                            newStudent.setGroup(Double.valueOf(groupNumber).intValue());
                        }
                    }

                    if (myCell.getColumnIndex() == 5) {
                        String email = myCell.toString().trim();
                        if (!email.equals("")) {
                            newStudent.setEmail(email);
                        } else {
                            isValidStudentInfo = false;
                        }
                    }
                }

                if (!((newStudent.getStudentNumber() == 0) || (newStudent.getFirstName() == null) || (newStudent.getLastName() == null)
                        || (newStudent.getEmail() == null))) {
                    if (newStudent.getMiddleName() == null) {
                        newStudent.setMiddleName("");
                    }

                    students.add(newStudent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    public ArrayList<Student> readXlsxStudents(String path) {
        ArrayList<Student> students = new ArrayList<>();

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

                Student newStudent = new Student();
                boolean isValidStudentInfo = true;

                while (cellIter.hasNext()) {
                    XSSFCell myCell = (XSSFCell) cellIter.next();
                    if (myCell.getColumnIndex() == 0) {
                        String studentID = myCell.toString().trim();

                        if (!studentID.equals("")) {
                            for (int i = 0; i < students.size(); i++) {
                                if (studentID.equals(students.get(i).getStudentNumber())) {
                                    isValidStudentInfo = false;
                                }
                            }
                            newStudent.setStudentNumber(Double.valueOf(studentID).intValue());
                        } else {
                            isValidStudentInfo = false;
                        }
                    }

                    if (myCell.getColumnIndex() == 1) {
                        String familyName = myCell.toString().trim();
                        if (!familyName.equals("")) {
                            newStudent.setLastName(familyName);
                        } else {
                            isValidStudentInfo = false;
                        }
                    }

                    if (myCell.getColumnIndex() == 2) {
                        String middleName = myCell.toString().trim();
                        if (!middleName.equals("")) {
                            newStudent.setMiddleName(middleName);
                        } else {
                            newStudent.setMiddleName("");
                        }
                    }

                    if (myCell.getColumnIndex() == 3) {
                        String givenName = myCell.toString().trim();
                        if (!givenName.equals("")) {
                            newStudent.setFirstName(givenName);
                        } else {
                            isValidStudentInfo = false;
                        }
                    }

                    if (myCell.getColumnIndex() == 4) {
                        String groupNumber = myCell.toString().trim();
                        if (!groupNumber.equals("")) {
                            newStudent.setGroup(Double.valueOf(myCell.toString().trim()).intValue());
                        }
                    }

                    if (myCell.getColumnIndex() == 5) {
                        String email = myCell.toString().trim();
                        if (!email.equals("")) {
                            newStudent.setEmail(email);
                        } else {
                            isValidStudentInfo = false;
                        }
                    }
                }

                if (!((newStudent.getStudentNumber() == 0) || (newStudent.getFirstName() == null) || (newStudent.getLastName() == null)
                        || (newStudent.getEmail() == null))) {
                    if (newStudent.getMiddleName() == null) {
                        newStudent.setMiddleName("");
                    }

                    students.add(newStudent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }
}
