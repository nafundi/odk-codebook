
package com.nafundi.taskforce.codebook.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Vector;

import org.javarosa.core.model.Constants;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.QuestionDef;
import org.javarosa.core.model.SelectChoice;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.model.xform.XFormsModule;
import org.javarosa.xform.parse.XFormParseException;
import org.javarosa.xform.util.XFormUtils;

public class CodebookEngine {

    public CodebookEngine() {
    }

    public ArrayList<CodebookEntry> LoadForm(String filename) {
        new XFormsModule().registerModule();
        // needed to override rms property manager
        org.javarosa.core.services.PropertyManager.setPropertyManager(new PropertyManager(5));

        File xml = new File(filename);
        String errorMsg = "";
        FormDef fd = null;
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

        return entries;

    }

    private void populateEntries(TreeElement t, FormDef fd, ArrayList<CodebookEntry> entries) {
        for (int i = 0; i < t.getNumChildren(); i++) {
            TreeElement t1 = t.getChildAt(i);
            CodebookEntry ce = new CodebookEntry();
            ce.setVariable(t1.getRef().toString(false));

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
                                values.append("User selected Date+Time");
                                break;
                            case Constants.DATATYPE_DATE:
                                values.append("User selected Date");
                                break;
                            case Constants.DATATYPE_TIME:
                                values.append("User selected Time");
                                break;
                            case Constants.DATATYPE_DECIMAL:
                                values.append("User entered decimal");
                                break;
                            case Constants.DATATYPE_INTEGER:
                                values.append("User entered integer");
                                break;
                            case Constants.DATATYPE_GEOPOINT:
                                values.append("System generated location coordinates");
                                break;
                            case Constants.DATATYPE_BARCODE:
                                values.append("User scanned barcode");
                                break;
                            case Constants.DATATYPE_TEXT:
                                values.append("User entered text");
                                break;
                        }
                        break;
                    case Constants.CONTROL_IMAGE_CHOOSE:
                        values.append("User selected/captured Image");
                        break;
                    case Constants.CONTROL_AUDIO_CAPTURE:
                        values.append("User selected/captured Audio");
                        break;
                    case Constants.CONTROL_VIDEO_CAPTURE:
                        values.append("User selected/captured Video");
                        break;
                    case Constants.CONTROL_SELECT_ONE:
                    case Constants.CONTROL_SELECT_MULTI:
                        Vector<SelectChoice> choices = qd.getChoices();
                        for (SelectChoice choice : choices) {
                            questions.append(choice.getLabelInnerText() + "\n");
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
                ce.setQuestion("not shown to user");
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
            return "system-generated upon opening form";
        } else if ("end".equalsIgnoreCase(params)) {
            return "system-generated upon completion of form";
        } else if ("deviceid".equalsIgnoreCase(params)) {
            return "system-generated device-specific IMEI";
        } else if ("subscriberid".equalsIgnoreCase(params)) {
            return "system-generated IMSI";
        } else if ("simserial".equalsIgnoreCase(params)) {
            return "system-generated serial number of SIM card";
        } else if ("phonenumber".equalsIgnoreCase(params)) {
            return "system-generated phone number of device or SIM card";
        } else if ("today".equalsIgnoreCase(params)) {
            return "system-generated todays date";
        } else {
            return "unknown preloader";
        }

    }

}
