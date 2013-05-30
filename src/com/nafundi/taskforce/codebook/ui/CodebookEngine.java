
package com.nafundi.taskforce.codebook.ui;

import org.javarosa.core.model.Constants;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.QuestionDef;
import org.javarosa.core.model.SelectChoice;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.model.xform.XFormsModule;
import org.javarosa.xform.parse.XFormParseException;
import org.javarosa.xform.util.XFormUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class CodebookEngine {

    private HashMap<String,String> metadata;
    public static String FILENAME = "filename";
    public static String LANGUAGE = "language";

    public ArrayList<CodebookEntry> loadForm(String filepath) {
        new XFormsModule().registerModule();
        // needed to override rms property manager
        org.javarosa.core.services.PropertyManager.setPropertyManager(new PropertyManager(5));

        File xml = new File(filepath);
        String errorMsg = "";
        FormDef fd = null;
        HashMap<String, String> fields = null;

        try {
            FileInputStream fis = new FileInputStream(xml);
            fd = XFormUtils.getFormFromInputStream(fis);
            if (fd == null) {
                errorMsg = "Error reading XForm file";
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            errorMsg = e.getMessage();
        } catch (XFormParseException e) {
            errorMsg = e.getMessage();
            e.printStackTrace();
        } catch (Exception e) {
            errorMsg = e.getMessage();
            e.printStackTrace();
        }
        if (!"".equals(errorMsg)) {
            System.out.println(errorMsg);
        }
        // new evaluation context for function handlers
        fd.setEvaluationContext(new EvaluationContext(null));
        fd.initialize(true);

        TreeElement te = fd.getInstance().getRoot();

        ArrayList<CodebookEntry> entries = new ArrayList<CodebookEntry>();
        populateEntries(te, fd, entries);

        String filenameWithExtension = xml.getName();
        String filename = filenameWithExtension.substring(0,filenameWithExtension.lastIndexOf('.'));
        metadata = new HashMap<String,String>();
        metadata.put(FILENAME,filename);
        metadata.put(LANGUAGE,fd.getLocalizer().getLocale());

        return entries;

    }

    private void populateEntries(TreeElement t, FormDef fd, ArrayList<CodebookEntry> entries) {
        for (int i = 0; i < t.getNumChildren(); i++) {
            TreeElement t1 = t.getChildAt(i);
            CodebookEntry ce = new CodebookEntry();
            String ref= t1.getRef().toString(false);
            
            // get rid of the leading path
            ce.setVariable(ref.substring(ref.lastIndexOf("/")+1));

            QuestionDef qd = FormDef.findQuestionByRef(t1.getRef(), fd);

            if (qd != null) {
                StringBuilder questions = new StringBuilder();
                StringBuilder values = new StringBuilder();

                // add question text
                questions.append(qd.getLabelInnerText() + "\n");

                // populate questions and values appropriately
                switch (qd.getControlType()) {
                    case Constants.CONTROL_INPUT:
                        switch (t1.dataType) {
                            case Constants.DATATYPE_DATE_TIME:
                                values.append("User selected date and time");
                                break;
                            case Constants.DATATYPE_DATE:
                                values.append("User selected date");
                                break;
                            case Constants.DATATYPE_TIME:
                                values.append("User selected time");
                                break;
                            case Constants.DATATYPE_DECIMAL:
                                values.append("User entered decimal");
                                break;
                            case Constants.DATATYPE_INTEGER:
                                values.append("User entered integer");
                                break;
                            case Constants.DATATYPE_GEOPOINT:
                                values.append("User captured location coordinates");
                                break;
                            case Constants.DATATYPE_BARCODE:
                                values.append("User captured barcode");
                                break;
                            case Constants.DATATYPE_TEXT:
                                values.append("User entered text");
                                break;
                        }
                        break;
                    case Constants.CONTROL_IMAGE_CHOOSE:
                        values.append("User captured image");
                        break;
                    case Constants.CONTROL_AUDIO_CAPTURE:
                        values.append("User captured audio");
                        break;
                    case Constants.CONTROL_VIDEO_CAPTURE:
                        values.append("User captured video");
                        break;
                    case Constants.CONTROL_SELECT_ONE:
                    case Constants.CONTROL_SELECT_MULTI:
                        values.append("\n");
                        Vector<SelectChoice> choices = qd.getChoices();
                        for (SelectChoice choice : choices) {
                            questions.append("\t"+ choice.getLabelInnerText() + "\n");
                            values.append(choice.getValue() + "\n");
                        }
                        break;
                    default:
                        break;
                }

                ce.setQuestion(questions.toString());
                ce.setValue(values.toString());
            } else {
                // if it's null, it's a preloader or a group
                ce.setQuestion("Hidden from user");
                ce.setValue(getValues(t1));
            }

            entries.add(ce);
            // recurse
            if (t1.getNumChildren() > 0) {
                populateEntries(t1, fd, entries);
            }
        }
    }

    private String getValues(TreeElement t) {
        String params = t.getPreloadParams();
        if (params == null) {
            // this was probably a group, so just return an empty string
            return "";
        }

        if ("start".equalsIgnoreCase(params)) {
            return "Timestamp of form open";
        } else if ("end".equalsIgnoreCase(params)) {
            return "Timestamp of form save";
        } else if ("today".equalsIgnoreCase(params)) {
            return "Today's date";
        } else if (PropertyManager.DEVICE_ID_PROPERTY.equalsIgnoreCase(params) || PropertyManager.OR_DEVICE_ID_PROPERTY.equalsIgnoreCase(params)) {
            return "Device ID (IMEI, Wi-Fi MAC, Android ID) ";
        } else if (PropertyManager.SUBSCRIBER_ID_PROPERTY.equalsIgnoreCase(params) || PropertyManager.OR_SUBSCRIBER_ID_PROPERTY.equalsIgnoreCase(params)) {
            return "Subscriber ID (IMSI)";
        } else if (PropertyManager.SIM_SERIAL_PROPERTY.equalsIgnoreCase(params) || PropertyManager.OR_SIM_SERIAL_PROPERTY.equalsIgnoreCase(params)) {
            return "Serial number of SIM";
        } else if (PropertyManager.PHONE_NUMBER_PROPERTY.equalsIgnoreCase(params) || PropertyManager.OR_PHONE_NUMBER_PROPERTY.equalsIgnoreCase(params)) {
            return "Phone number of SIM";
        } else if (PropertyManager.USERNAME.equalsIgnoreCase(params) || PropertyManager.OR_USERNAME.equalsIgnoreCase(params)) {
            return "Username on device";
        } else if (PropertyManager.EMAIL.equalsIgnoreCase(params) || PropertyManager.OR_EMAIL.equalsIgnoreCase(params)) {
            return "Google account on device";
        } else {
            return "Unknown preloader";
        }

    }

    public HashMap<String, String> getMetadata() {
        return metadata;
    }
}

