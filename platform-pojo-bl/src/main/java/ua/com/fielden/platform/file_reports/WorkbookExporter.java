package ua.com.fielden.platform.file_reports;

import static org.apache.commons.lang.StringUtils.join;
import static ua.com.fielden.platform.domaintree.impl.AbstractDomainTreeRepresentation.isShortCollection;
import static ua.com.fielden.platform.reflection.Finder.getKeyMembers;
import static ua.com.fielden.platform.reflection.PropertyTypeDeterminator.determinePropertyType;
import static ua.com.fielden.platform.reflection.PropertyTypeDeterminator.isDotNotation;
import static ua.com.fielden.platform.reflection.PropertyTypeDeterminator.penultAndLast;
import static ua.com.fielden.platform.reflection.asm.impl.DynamicEntityClassLoader.getOriginalType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.Deflater;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.joda.time.DateTime;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.serialisation.xstream.GZipOutputStreamEx;
import ua.com.fielden.platform.utils.Pair;

/**
 * A set of utility methods for exporting data into MS Excel.
 *
 * @author TG Team
 *
 */
public class WorkbookExporter {

    public static <M extends AbstractEntity<?>> HSSFWorkbook export(final List<M> entities, final String[] propertyNames, final String[] propertyTitles) {
        final List<Pair<String, String>> propNamesAndTitles = new ArrayList<>();

        for (int index = 0; index < propertyNames.length && index < propertyTitles.length; index++) {
            propNamesAndTitles.add(new Pair<String, String>(propertyNames[index], propertyTitles[index]));
        }
        final DataForWorkbookSheet<M> dataForWorkbookSheet = new DataForWorkbookSheet<M>("Exported data", entities, propNamesAndTitles);
        final List<DataForWorkbookSheet<? extends AbstractEntity<?>>> sheetsData = new ArrayList<>();
        sheetsData.add(dataForWorkbookSheet);
        return export(sheetsData);
    }

    public static byte[] convertToGZipByteArray(final HSSFWorkbook workbook) throws IOException {
        final ByteArrayOutputStream oStream = new ByteArrayOutputStream();
        final GZipOutputStreamEx zOut = new GZipOutputStreamEx(oStream, Deflater.BEST_COMPRESSION);
        workbook.write(zOut);
        zOut.flush();
        zOut.close();
        oStream.flush();
        oStream.close();
        return oStream.toByteArray();
    }

    public static byte[] convertToByteArray(final HSSFWorkbook workbook) throws IOException {
        final ByteArrayOutputStream oStream = new ByteArrayOutputStream();
        workbook.write(oStream);
        oStream.flush();
        oStream.close();
        return oStream.toByteArray();
    }

    public static HSSFWorkbook export(final List<DataForWorkbookSheet<? extends AbstractEntity<?>>> sheetsData) {
        final HSSFWorkbook wb = new HSSFWorkbook();
        for (final DataForWorkbookSheet<? extends AbstractEntity<?>> sheetData : sheetsData) {
            addSheetWithData(wb, sheetData);
        }
        return wb;
    }

    private static <M extends AbstractEntity<?>> void addSheetWithData(final HSSFWorkbook wb, final DataForWorkbookSheet<M> sheetData) {
        final HSSFSheet sheet = wb.createSheet(sheetData.getSheetTitle());
        // Create a header row.
        final HSSFRow headerRow = sheet.createRow(0);
        // Create a new font and alter it
        final HSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 11);
        font.setFontName("Courier New");
        font.setBoldweight((short) 1000);
        // Fonts are set into a style so create a new one to use
        final HSSFCellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setFont(font);
        headerCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        headerCellStyle.setWrapText(true);
        final HSSFCellStyle headerInnerCellStyle = wb.createCellStyle();
        headerInnerCellStyle.setFont(font);
        headerInnerCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        headerInnerCellStyle.setBorderRight(HSSFCellStyle.BORDER_HAIR);
        headerInnerCellStyle.setWrapText(true);
        // Create cells and put column names there
        for (int index = 0; index < sheetData.getPropTitles().size(); index++) {
            final HSSFCell cell = headerRow.createCell(index);
            cell.setCellValue(sheetData.getPropTitles().get(index));
            cell.setCellStyle(index < sheetData.getPropTitles().size() - 1 ? headerInnerCellStyle : headerCellStyle);
        }

        final CellStyle dateCellStyle = wb.createCellStyle();
        final CreationHelper createHelper = wb.getCreationHelper();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));

        // let's make cell style to handle borders
        final HSSFCellStyle dataCellStyle = wb.createCellStyle();
        dataCellStyle.setBorderRight(HSSFCellStyle.BORDER_HAIR);
        for (int index = 0; index < sheetData.getEntities().size(); index++) {
            final HSSFRow row = sheet.createRow(index + 1); // new row starting with 1
            // iterate through values in the current table row and populate the sheet row
            for (int propIndex = 0; propIndex < sheetData.getPropNames().size(); propIndex++) {
                final HSSFCell cell = row.createCell(propIndex); // create new cell
                if (propIndex < sheetData.getPropNames().size() - 1) { // the last column should not have right border
                    cell.setCellStyle(dataCellStyle);
                }
                final AbstractEntity<?> entity = sheetData.getEntities().get(index);
                final String propertyName = sheetData.getPropNames().get(propIndex);
                final Object value = entity.get(propertyName); // get the value
                // need to try to do the best job with types
                if (value instanceof Date) {
                    cell.setCellValue((Date) value);
                    cell.setCellStyle(dateCellStyle);
                } else if (value instanceof DateTime) {
                    cell.setCellValue(((DateTime) value).toDate());
                    cell.setCellStyle(dateCellStyle);
                } else if (value instanceof Number) {
                    cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    cell.setCellValue(((Number) value).doubleValue());
                } else if (value instanceof Boolean) {
                    cell.setCellType(HSSFCell.CELL_TYPE_BOOLEAN);
                    cell.setCellValue((Boolean) value);
                } else if (value == null) { // if null then leave call blank
                    cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
                } else { // otherwise treat value as String
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    if (isShortCollection(entity.getType(), propertyName)) {
                        cell.setCellValue(join(createShortColection(entity, propertyName), ", "));
                    } else {
                        cell.setCellValue(value.toString());
                    }
                }
            }
        }

        // adjusting columns widths
        for (int propIndex = 0; propIndex < sheetData.getPropNames().size(); propIndex++) {
            sheet.autoSizeColumn(propIndex);
            sheet.setColumnWidth(propIndex, (int) (sheet.getColumnWidth(propIndex) * 1.05));
        }

        // tripling first row height
        sheet.getRow(0).setHeight((short) (sheet.getRow(0).getHeight() * 3));

        // freezing first row
        sheet.createFreezePane(0, 1);
    }

    /**
     * Analysis the value of the short collectional property and returns the list of key entities those are not equal to entity that contains the collectional property.
     *
     * @param entity
     * @param propertyName
     * @return
     */
    @SuppressWarnings("unchecked")
    private static List<AbstractEntity<?>> createShortColection(final AbstractEntity<?> entity, final String propertyName) {
        final Collection<AbstractEntity<?>> value = (Collection<AbstractEntity<?>>) entity.get(propertyName);
        final AbstractEntity<?> containerEntity = isDotNotation(propertyName) ? (AbstractEntity<?>) entity.get(penultAndLast(propertyName).getKey()) : entity;
        final List<Field> keyMemebers = getKeyMembers(determinePropertyType(entity.getType(), propertyName));
        final String keyElement = keyMemebers.stream().filter(field -> !field.getType().equals(getOriginalType(containerEntity.getType())))
                .findFirst().map(findField -> findField.getName()).get();
        return value.stream().map(entityElement -> (AbstractEntity<?>) entityElement.get(keyElement)).collect(Collectors.toList());
    }
}
