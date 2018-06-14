import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.util.regex.Pattern;
import java.io.*;
import jxl.Workbook;

public class xml_homework_1 {


	public static void OutToExcel(){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        try{
            WritableWorkbook book = Workbook.createWorkbook(new File("out1.xls"));
            WritableSheet sheet = book.createSheet("sheet1",0);
            Label id = new Label(0,0,"id");
            sheet.addCell(id);
            Label name = new Label(1,0,"name");
            sheet.addCell(name);
            Label time = new Label(2,0,"time");
            sheet.addCell(time);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(".//work1.xml");
            Element root = document.getDocumentElement();
            NodeList pageList = root.getElementsByTagName("page");
            int iCnt;
            for(iCnt = 0; iCnt < pageList.getLength(); iCnt++) {
                Element pageNode = (Element) pageList.item(iCnt);
                NodeList pt = pageNode.getElementsByTagName("pagetitle");
                String pagetitles = pt.item(0).getFirstChild().getNodeValue();
                Number no = new Number(0,iCnt+1,iCnt+1);
                sheet.addCell(no);
                Label pageT = new Label(1,iCnt+1,pagetitles);
                sheet.addCell(pageT);
                NodeList titlelist = pageNode.getElementsByTagName("title");
                for(int jCnt = 0;jCnt < titlelist.getLength();jCnt++){
                    Node node = titlelist.item(jCnt);
                    NodeList timelist = node.getChildNodes();
                    Pattern r = Pattern.compile("Use (.*) dates");
                    if(r.matcher(timelist.item(0).getNodeValue()).find()){
                         Element nodetemp = (Element) node.getParentNode();
                         NodeList nltemp = nodetemp.getElementsByTagName("value");
                         Label date = new Label(2,iCnt+1,nltemp.item(0).getFirstChild().getNodeValue());
                         sheet.addCell(date);
                    }
                }
            }
            book.write();
            book.close();
        }
        catch (Exception e){
            System.out.println(e);
        }
	}

    public static void main(String args[]){
        OutToExcel();
	}
}
