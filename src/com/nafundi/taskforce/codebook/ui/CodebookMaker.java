package com.nafundi.taskforce.codebook.ui;

import com.googlecode.jatl.Html;
import com.lowagie.text.DocumentException;
import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;

import static java.util.Arrays.asList;

/**
 * Created by yanokwa on 5/29/13.
 */
public class CodebookMaker extends SwingWorker {

    private ArrayList<CodebookEntry> codebookEntries;
    private String locale;
    private String inputFilename;
    private String outputFolderPath;

    public CodebookMaker(ArrayList<CodebookEntry> codebookEntries, String locale, String inputFilename, String outputFolderPath) {
        this.codebookEntries = codebookEntries;
        this.locale = locale;
        this.inputFilename = inputFilename;
        this.outputFolderPath = outputFolderPath;
    }

    public Integer doInBackground() {

        // string writer
        StringWriter writer = new StringWriter();
        new Html(writer) {{

            head();
            meta().charset("utf-8").end();
            // bootstrap css with only headings, body type, and tables
            // also have custom tr.gray tag to fix bug in html to pdf export
            style().type("text/css").text(".clearfix{*zoom:1;}.clearfix:before,.clearfix:after{display:table;content:\"\";line-height:0;}\n" +
                    ".clearfix:after{clear:both;}\n" +
                    ".hide-text{font:0/0 a;color:transparent;text-shadow:none;background-color:transparent;border:0;}\n" +
                    ".input-block-level{display:block;width:100%;min-height:30px;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;}\n" +
                    "body{margin:0;font-family:\"Lucida Sans Unicode\",Helvetica,Arial,sans-serif;font-size:14px;line-height:20px;color:#333333;background-color:#ffffff;}\n" +
                    "a{color:#0088cc;text-decoration:none;}\n" +
                    "a:hover,a:focus{color:#005580;text-decoration:underline;}\n" +
                    ".img-rounded{-webkit-border-radius:6px;-moz-border-radius:6px;border-radius:6px;}\n" +
                    ".img-polaroid{padding:4px;background-color:#fff;border:1px solid #ccc;border:1px solid rgba(0, 0, 0, 0.2);-webkit-box-shadow:0 1px 3px rgba(0, 0, 0, 0.1);-moz-box-shadow:0 1px 3px rgba(0, 0, 0, 0.1);box-shadow:0 1px 3px rgba(0, 0, 0, 0.1);}\n" +
                    ".img-circle{-webkit-border-radius:500px;-moz-border-radius:500px;border-radius:500px;}\n" +
                    "p{margin:0 0 10px;}\n" +
                    ".lead{margin-bottom:20px;font-size:21px;font-weight:200;line-height:30px;}\n" +
                    "small{font-size:85%;}\n" +
                    "strong{font-weight:bold;}\n" +
                    "em{font-style:italic;}\n" +
                    "cite{font-style:normal;}\n" +
                    ".muted{color:#999999;}\n" +
                    "a.muted:hover,a.muted:focus{color:#808080;}\n" +
                    ".text-warning{color:#c09853;}\n" +
                    "a.text-warning:hover,a.text-warning:focus{color:#a47e3c;}\n" +
                    ".text-error{color:#b94a48;}\n" +
                    "a.text-error:hover,a.text-error:focus{color:#953b39;}\n" +
                    ".text-info{color:#3a87ad;}\n" +
                    "a.text-info:hover,a.text-info:focus{color:#2d6987;}\n" +
                    ".text-success{color:#468847;}\n" +
                    "a.text-success:hover,a.text-success:focus{color:#356635;}\n" +
                    ".text-left{text-align:left;}\n" +
                    ".text-right{text-align:right;}\n" +
                    ".text-center{text-align:center;}\n" +
                    "h1,h2,h3,h4,h5,h6{margin:10px 0;font-family:inherit;font-weight:bold;line-height:20px;color:inherit;text-rendering:optimizelegibility;}h1 small,h2 small,h3 small,h4 small,h5 small,h6 small{font-weight:normal;line-height:1;color:#999999;}\n" +
                    "h1,h2,h3{line-height:40px;}\n" +
                    "h1{font-size:38.5px;}\n" +
                    "h2{font-size:31.5px;}\n" +
                    "h3{font-size:24.5px;}\n" +
                    "h4{font-size:17.5px;}\n" +
                    "h5{font-size:14px;}\n" +
                    "h6{font-size:11.9px;}\n" +
                    "h1 small{font-size:24.5px;}\n" +
                    "h2 small{font-size:17.5px;}\n" +
                    "h3 small{font-size:14px;}\n" +
                    "h4 small{font-size:14px;}\n" +
                    ".page-header{padding-bottom:9px;margin:20px 0 30px;border-bottom:1px solid #eeeeee;}\n" +
                    "ul,ol{padding:0;margin:0 0 10px 25px;}\n" +
                    "ul ul,ul ol,ol ol,ol ul{margin-bottom:0;}\n" +
                    "li{line-height:20px;}\n" +
                    "ul.unstyled,ol.unstyled{margin-left:0;list-style:none;}\n" +
                    "ul.inline,ol.inline{margin-left:0;list-style:none;}ul.inline>li,ol.inline>li{display:inline-block;*display:inline;*zoom:1;padding-left:5px;padding-right:5px;}\n" +
                    "dl{margin-bottom:20px;}\n" +
                    "dt,dd{line-height:20px;}\n" +
                    "dt{font-weight:bold;}\n" +
                    "dd{margin-left:10px;}\n" +
                    ".dl-horizontal{*zoom:1;}.dl-horizontal:before,.dl-horizontal:after{display:table;content:\"\";line-height:0;}\n" +
                    ".dl-horizontal:after{clear:both;}\n" +
                    ".dl-horizontal dt{float:left;width:160px;clear:left;text-align:right;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;}\n" +
                    ".dl-horizontal dd{margin-left:180px;}\n" +
                    "hr{margin:20px 0;border:0;border-top:1px solid #eeeeee;border-bottom:1px solid #ffffff;}\n" +
                    "abbr[title],abbr[data-original-title]{cursor:help;border-bottom:1px dotted #999999;}\n" +
                    "abbr.initialism{font-size:90%;text-transform:uppercase;}\n" +
                    "blockquote{padding:0 0 0 15px;margin:0 0 20px;border-left:5px solid #eeeeee;}blockquote p{margin-bottom:0;font-size:17.5px;font-weight:300;line-height:1.25;}\n" +
                    "blockquote small{display:block;line-height:20px;color:#999999;}blockquote small:before{content:'\\2014 \\00A0';}\n" +
                    "blockquote.pull-right{float:right;padding-right:15px;padding-left:0;border-right:5px solid #eeeeee;border-left:0;}blockquote.pull-right p,blockquote.pull-right small{text-align:right;}\n" +
                    "blockquote.pull-right small:before{content:'';}\n" +
                    "blockquote.pull-right small:after{content:'\\00A0 \\2014';}\n" +
                    "q:before,q:after,blockquote:before,blockquote:after{content:\"\";}\n" +
                    "address{display:block;margin-bottom:20px;font-style:normal;line-height:20px;}\n" +
                    "table{max-width:100%;background-color:transparent;border-collapse:collapse;border-spacing:0;}\n" +
                    ".table{width:100%;margin-bottom:20px;}.table th,.table td{padding:8px;line-height:20px;text-align:left;vertical-align:top;border-top:1px solid #dddddd;}\n" +
                    ".table th{font-weight:bold;}\n" +
                    ".table thead th{vertical-align:bottom;}\n" +
                    ".table caption+thead tr:first-child th,.table caption+thead tr:first-child td,.table colgroup+thead tr:first-child th,.table colgroup+thead tr:first-child td,.table thead:first-child tr:first-child th,.table thead:first-child tr:first-child td{border-top:0;}\n" +
                    ".table tbody+tbody{border-top:2px solid #dddddd;}\n" +
                    ".table .table{background-color:#ffffff;}\n" +
                    ".table-condensed th,.table-condensed td{padding:4px 5px;}\n" +
                    ".table-bordered{border:1px solid #dddddd;border-collapse:separate;*border-collapse:collapse;border-left:0;-webkit-border-radius:4px;-moz-border-radius:4px;border-radius:4px;}.table-bordered th,.table-bordered td{border-left:1px solid #dddddd;}\n" +
                    ".table-bordered caption+thead tr:first-child th,.table-bordered caption+tbody tr:first-child th,.table-bordered caption+tbody tr:first-child td,.table-bordered colgroup+thead tr:first-child th,.table-bordered colgroup+tbody tr:first-child th,.table-bordered colgroup+tbody tr:first-child td,.table-bordered thead:first-child tr:first-child th,.table-bordered tbody:first-child tr:first-child th,.table-bordered tbody:first-child tr:first-child td{border-top:0;}\n" +
                    ".table-bordered thead:first-child tr:first-child>th:first-child,.table-bordered tbody:first-child tr:first-child>td:first-child,.table-bordered tbody:first-child tr:first-child>th:first-child{-webkit-border-top-left-radius:4px;-moz-border-radius-topleft:4px;border-top-left-radius:4px;}\n" +
                    ".table-bordered thead:first-child tr:first-child>th:last-child,.table-bordered tbody:first-child tr:first-child>td:last-child,.table-bordered tbody:first-child tr:first-child>th:last-child{-webkit-border-top-right-radius:4px;-moz-border-radius-topright:4px;border-top-right-radius:4px;}\n" +
                    ".table-bordered thead:last-child tr:last-child>th:first-child,.table-bordered tbody:last-child tr:last-child>td:first-child,.table-bordered tbody:last-child tr:last-child>th:first-child,.table-bordered tfoot:last-child tr:last-child>td:first-child,.table-bordered tfoot:last-child tr:last-child>th:first-child{-webkit-border-bottom-left-radius:4px;-moz-border-radius-bottomleft:4px;border-bottom-left-radius:4px;}\n" +
                    ".table-bordered thead:last-child tr:last-child>th:last-child,.table-bordered tbody:last-child tr:last-child>td:last-child,.table-bordered tbody:last-child tr:last-child>th:last-child,.table-bordered tfoot:last-child tr:last-child>td:last-child,.table-bordered tfoot:last-child tr:last-child>th:last-child{-webkit-border-bottom-right-radius:4px;-moz-border-radius-bottomright:4px;border-bottom-right-radius:4px;}\n" +
                    ".table-bordered tfoot+tbody:last-child tr:last-child td:first-child{-webkit-border-bottom-left-radius:0;-moz-border-radius-bottomleft:0;border-bottom-left-radius:0;}\n" +
                    ".table-bordered tfoot+tbody:last-child tr:last-child td:last-child{-webkit-border-bottom-right-radius:0;-moz-border-radius-bottomright:0;border-bottom-right-radius:0;}\n" +
                    ".table-bordered caption+thead tr:first-child th:first-child,.table-bordered caption+tbody tr:first-child td:first-child,.table-bordered colgroup+thead tr:first-child th:first-child,.table-bordered colgroup+tbody tr:first-child td:first-child{-webkit-border-top-left-radius:4px;-moz-border-radius-topleft:4px;border-top-left-radius:4px;}\n" +
                    ".table-bordered caption+thead tr:first-child th:last-child,.table-bordered caption+tbody tr:first-child td:last-child,.table-bordered colgroup+thead tr:first-child th:last-child,.table-bordered colgroup+tbody tr:first-child td:last-child{-webkit-border-top-right-radius:4px;-moz-border-radius-topright:4px;border-top-right-radius:4px;}\n" +
                    ".table-striped tbody>tr:nth-child(odd)>td,.table-striped tbody>tr:nth-child(odd)>th{background-color:#f9f9f9;}\n" +
                    ".table-hover tbody tr:hover>td,.table-hover tbody tr:hover>th{background-color:#f5f5f5;}\n" +
                    "table td[class*=\"span\"],table th[class*=\"span\"],.row-fluid table td[class*=\"span\"],.row-fluid table th[class*=\"span\"]{display:table-cell;float:none;margin-left:0;}\n" +
                    ".table td.span1,.table th.span1{float:none;width:44px;margin-left:0;}\n" +
                    ".table td.span2,.table th.span2{float:none;width:124px;margin-left:0;}\n" +
                    ".table td.span3,.table th.span3{float:none;width:204px;margin-left:0;}\n" +
                    ".table td.span4,.table th.span4{float:none;width:284px;margin-left:0;}\n" +
                    ".table td.span5,.table th.span5{float:none;width:364px;margin-left:0;}\n" +
                    ".table td.span6,.table th.span6{float:none;width:444px;margin-left:0;}\n" +
                    ".table td.span7,.table th.span7{float:none;width:524px;margin-left:0;}\n" +
                    ".table td.span8,.table th.span8{float:none;width:604px;margin-left:0;}\n" +
                    ".table td.span9,.table th.span9{float:none;width:684px;margin-left:0;}\n" +
                    ".table td.span10,.table th.span10{float:none;width:764px;margin-left:0;}\n" +
                    ".table td.span11,.table th.span11{float:none;width:844px;margin-left:0;}\n" +
                    ".table td.span12,.table th.span12{float:none;width:924px;margin-left:0;}\n" +
                    ".table tbody tr.success>td{background-color:#dff0d8;}\n" +
                    ".table tbody tr.error>td{background-color:#f2dede;}\n" +
                    ".table tbody tr.warning>td{background-color:#fcf8e3;}\n" +
                    ".table tbody tr.info>td{background-color:#d9edf7;}\n" +
                    ".table tbody tr.gray>td{background-color:#f9f9f9;}\n" +
                    ".table-hover tbody tr.success:hover>td{background-color:#d0e9c6;}\n" +
                    ".table-hover tbody tr.error:hover>td{background-color:#ebcccc;}\n" +
                    ".table-hover tbody tr.warning:hover>td{background-color:#faf2cc;}\n" +
                    ".table-hover tbody tr.info:hover>td{background-color:#c4e3f3;}\n").end();
            end();
            body();
            h2().text(inputFilename + " (" + locale + ")").end();
            table().classAttr("table table-striped table-bordered");
            thead().tr();
            for (String header : asList("Variable Name", "Question Text", "Saved Value")) {
                th().text(header).end();
            }
            end().end();
            tbody();
            for (int i = 0; i < codebookEntries.size(); i++) {
                if (i % 2 == 0) {
                    // fix for no background color in export to pdf
                    tr().classAttr("gray");
                } else {
                    tr();
                }
                CodebookEntry entry = codebookEntries.get(i);
                String variable = entry.getVariable();
                // add newlines and tabs
                String question = entry.getQuestion();
                String value = entry.getValue();

                for (String cell : asList(variable, question, value)) {
                    td().text(cell).end();
                }
                end();
            }
            done();
        }};

        // build html string
        String htmlHeader = "<!DOCTYPE html>\n<html>\n";
        String htmlFooter = "\n</html>";
        String htmlDocument = htmlHeader + StringEscapeUtils.unescapeHtml(writer.getBuffer().toString()) + htmlFooter;

        // move html into document
        DocumentBuilder builder = null;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document document = null;
        try {
            document = builder.parse(new ByteArrayInputStream(htmlDocument.getBytes("UTF-8")));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create render of document
        // ITextRender is not thread-safe
        synchronized (this) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocument(document, null);
            renderer.layout();

            // TODO: asian characters are in html but don't show up in pdf
            // write out document as pdf
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(outputFolderPath + File.separator + inputFilename + " (" + locale + ").pdf");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                renderer.createPDF(outputStream);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return 0;
    }
}
