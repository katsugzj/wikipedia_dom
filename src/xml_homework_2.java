import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableWorkbook;
import jxl.write.WritableSheet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.util.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jxl.Workbook;

class root{
    TreeMap pagelist;
    public root(){pagelist = new TreeMap();}
    page findpage(String findkey){return (page) pagelist.get(findkey);}
}

class page{
    String pagetitle;
    TreeMap textlist;
    public page(){textlist = new TreeMap();}
    page (String pagetitle) {this.pagetitle = pagetitle;}
    text findtext(String findkey){
//        if(findkey.contains(" ")){
//            findkey = findkey.substring(0,findkey.indexOf(" "));
//        }
        return (text) textlist.get(findkey);
    }
}

class text{
    String text;
    int num;
    text(String text){this.text = text;}
    void num(int num){this.num = num;}
    int getNum(String text){return num;};
}

public class xml_homework_2 {

	public static root parseinput(){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        try{
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(".//work1.xml");
            Element root = document.getDocumentElement();
            NodeList pageList = root.getElementsByTagName("page");
            root roots = new root();
            int iCnt;
            for(iCnt = 0; iCnt < pageList.getLength(); iCnt++) {
                Element pageNode = (Element) pageList.item(iCnt);
                NodeList pt = pageNode.getElementsByTagName("pagetitle");
                String pagetitles = pt.item(0).getFirstChild().getNodeValue();
                NodeList textlist = pageNode.getChildNodes();
                for (int i = 0; i < textlist.getLength(); i++) {
                    if (textlist.item(i).getNodeType() == 3 && textlist.item(i).getNodeValue().contains("[")) {
                        Pattern r = Pattern.compile("(\\[.*?\\])");
                        Matcher m = r.matcher(textlist.item(i).getNodeValue());
                        while (m.find()) {
                            String re = m.group(0);
                            re = re.replaceAll("\\[|\\]|http(.*?) |Category:|(.*?)//(.*?) ", "");
                            if (re.split("\\s").length < 4) {
                                int tag = re.lastIndexOf("|");
                                if (tag != -1) {
                                    re = re.substring(tag + 1);
                                }
                                if (re.split("\\W").length != 0) {
                                    int temp = 0;
                                    page p = roots.findpage(pagetitles);
                                    if (p == null) {
                                        p = new page();
                                    }
                                    roots.pagelist.put(pagetitles, p);
                                    text t = p.findtext(re);
                                    if(t == null){
                                        t = new text(re);
                                        t.num(1);
                                    }
                                    else{
                                        temp = t.getNum(re);
                                        t.num(temp+1);
                                    }
                                    p.textlist.put(re, t);
                                }
                            }
                        }
                    }
                }
            }
            return roots;
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }
	}
	public static void op(root roots){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        try {
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document document = docBuilder.newDocument();
            Element root = document.createElement("root");

            Iterator ipage = roots.pagelist.keySet().iterator();
            while (ipage.hasNext()) {
                String pageT = (String) ipage.next();
                page p = (page) roots.pagelist.get(pageT);
                Element Page = document.createElement("page");
                Page.setAttribute("pagetitle", pageT);
                Iterator iText = p.textlist.keySet().iterator();
                while (iText.hasNext()) {
                    String txt = (String) iText.next();
                    text Txt = (text) p.textlist.get(txt);
                    Element text = document.createElement("text");
                    text.setAttribute("count",Txt.num+"");
                    Text txtnode = document.createTextNode(txt);
                    text.appendChild(txtnode);
                    Page.appendChild(text);
                }
                root.appendChild(Page);
            }
            document.appendChild(root);
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            File file = new File("out.xml");
            FileOutputStream out = new FileOutputStream(file);
            StreamResult xmlResult = new StreamResult(out);
            transformer.transform(domSource, xmlResult);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public static void count(){
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    dbf.setValidating(false);
	    try{
	        String out[][] = new String[10000][3];
	        String tempTxt[] = new String[100];
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(".//out.xml");
            Element root = document.getDocumentElement();
            NodeList pageList = root.getElementsByTagName("page");
            int iCnt;
            int offset=0;
            int flag;
            int flagtemp;
            int cnt= 0;
            for(iCnt = 0; iCnt < pageList.getLength(); iCnt++) {
                int offsettemp=0;
                flagtemp=offset;
                String pagetile = pageList.item(iCnt).getAttributes().item(0).getNodeValue();
                Element textEle = (Element) pageList.item(iCnt);
                //System.out.println(pagetile);
                NodeList textList = textEle.getElementsByTagName("text");
                for(int jCnt=0 ; jCnt<textList.getLength();jCnt++) {
                    String temp = textList.item(jCnt).getAttributes().item(0).toString();
                    int count = Integer.valueOf(temp.substring(temp.indexOf("\"") + 1, temp.lastIndexOf("\"")));
                    if (count > 2) {
                        cnt ++;
                        //System.out.println(temp.substring(temp.indexOf("\"")+1,temp.lastIndexOf("\"")));
                        //System.out.println(textList.item(jCnt).getFirstChild().getNodeValue());
                        String txtTemp = textList.item(jCnt).getFirstChild().getNodeValue();
                        flag = flagtemp;
                        if (pagetile.length() < txtTemp.length()) {
                            while (flag != 0) {
                                if (out[flag][0] == pagetile && out[flag][1] == txtTemp) {
                                    out[flag][2] = Integer.valueOf(out[flag][2]) + count + "";
                                    break;
                                }
                                flag--;
                            }
                            if (flag == 0) {
                                out[offset][0] = pagetile;
                                out[offset][1] = txtTemp;
                                out[offset++][2] = count + "";
                                tempTxt[offsettemp] = txtTemp;
                            }
                        } else {
                            while (flag != 0) {
                                if (out[flag][0] == txtTemp && out[flag][1] == pagetile) {
                                    out[flag][2] = Integer.valueOf(out[flag][2]) + count + "";
                                    break;
                                }
                                flag--;
                            }
                            if (flag == 0) {
                                out[offset][0] = txtTemp;
                                out[offset][1] = pagetile;
                                out[offset++][2] = count + "";
                                tempTxt[offsettemp] = txtTemp;
                            }
                        }
                        offsettemp++;
                    }
                }
                flagtemp = offset;
                for (int jCnt = 0; jCnt < offsettemp; jCnt++)
                    for (int kCnt = jCnt+1; kCnt < offsettemp; kCnt++) {
                        flag = flagtemp;
                        if (tempTxt[jCnt].toString().length() < tempTxt[kCnt].toString().length()) {
                            while (flag != 0) {
                                if (out[flag][0] == tempTxt[jCnt] && out[flag][1] == tempTxt[kCnt]) {
                                    out[flag][2] = Integer.valueOf(out[flag][2]) + 1 + "";
                                    break;
                                }
                                flag--;
                            }
                            if (flag == 0) {
                                out[offset][0] = tempTxt[jCnt];
                                out[offset][1] = tempTxt[kCnt];
                                out[offset++][2] = 1+"";
                            }
                        }
                        else {
                            while (flag != 0) {
                                if (out[flag][0] == tempTxt[kCnt] && out[flag][1] == tempTxt[jCnt]) {
                                    out[flag][2] = Integer.valueOf(out[flag][2]) + 1 + "";
                                    break;
                                }
                                flag--;
                            }
                            if (flag == 0) {
                                out[offset][0] = tempTxt[kCnt];
                                out[offset][1] = tempTxt[jCnt];
                                out[offset++][2] = 1+"";
                            }
                        }
                    }
            }
            WritableWorkbook book = Workbook.createWorkbook(new File("out2.xls"));
            WritableSheet sheet = book.createSheet("sheet1",0);
            Label name1 = new Label(0,0,"name");
            sheet.addCell(name1);
            Label name2 = new Label(1,0,"name");
            sheet.addCell(name2);
            Label label = new Label(2,0,"label");
            sheet.addCell(label);
            for(iCnt=0;iCnt<offset;iCnt++){
                Label name1st = new Label(0,iCnt+1,out[iCnt][0]);
                sheet.addCell(name1st);
                Label name2nd = new Label(1,iCnt+1,out[iCnt][1]);
                sheet.addCell(name2nd);
                Number res = new Number(2,iCnt+1,Integer.valueOf(out[iCnt][2]));
                sheet.addCell(res);
            }
            book.write();
            book.close();
            System.out.println(cnt);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public static void main(String args[]){
        //op(parseinput());
	    count();
	}
}
