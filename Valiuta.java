
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Valiuta {

    static List<String> valueList = new ArrayList<String>();

    public static void main(String args[]) {

        //Kintamieji
        DateFormat formatas = new SimpleDateFormat("yyyy-MM-dd");

        String salys[] = {"AUD", "BGN", "BRL", "CAD", "CHF", "CNY", "CZK", "DKK", "GBP", "HKD",
            "HRK", "HUF", "IDR", "ILS", "INR", "ISK", "JPY", "KRW", "MXN", "MYR", "NOK", "NZD",
            "PHP", "PLN", "RON", "RUB", "SEK", "SGD", "THB", "TRY", "USD", "ZAR"};

        //JFrame kintamieji               
        JFrame f = new JFrame("Kursai");

        f.getContentPane().setLayout(new FlowLayout());

        JComboBox kodai = new JComboBox(salys);
        JLabel label1 = new JLabel("Data nuo:");
        JLabel label2 = new JLabel("Data iki:");

        JFormattedTextField dataNuo = new JFormattedTextField(formatas);
        dataNuo.setColumns(10);

        JFormattedTextField dataIki = new JFormattedTextField(formatas);
        dataIki.setColumns(10);

        JButton gauti = new JButton("Tikrinti kursą");

        f.add(label1);
        f.add(dataNuo);
        f.add(label2);
        f.add(dataIki);
        f.add(kodai);
        f.add(gauti);

        f.setSize(500, 100);
        f.setVisible(true);

        gauti.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                try {

                    //priskiriami kintamieji Date tipui
                    Date datanuo1 = new SimpleDateFormat("yyyy-MM-dd").parse(dataNuo.getText());
                    Date dataiki1 = new SimpleDateFormat("yyyy-MM-dd").parse(dataIki.getText());

                    //tikrina, ar datos nėra atvirkščiai suvestos
                    if (datanuo1.before(dataiki1)) {
                        try {

                            //kviečiam metodą
                            veiksmai(dataNuo.getText(), dataIki.getText(), (String) kodai.getSelectedItem());

                            //gautus duomenis iš list'o keičiam iš string'ū į double
                            int a = valueList.size() - 2;

                            NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);

                            Number number = format.parse(valueList.get(a));
                            double b = number.doubleValue();

                            Number number1 = format.parse(valueList.get(2));
                            double c = number1.doubleValue();

                            //kurso pasikeitimo santykis
                            double di = c - b;

                            String dif = String.valueOf(di);

                            //spausdinam rezultatą
                            String dialog = dataIki.getText() + " " + (String) kodai.getSelectedItem() + " kursas yra: " + number1 + ". Tarp " + dataNuo.getText() + " ir " + dataIki.getText() + " pasikeitė santykiu: " + dif;
                            JDialog d = new JDialog(f, "dialog Box");
                            d.getContentPane().setLayout(new FlowLayout());
                            d.setSize(800, 100);
                            d.setVisible(true);
                            JLabel l = new JLabel(dialog);
                            d.add(l);

                        } catch (TransformerException ex) {
                            Logger.getLogger(Valiuta.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (SAXException ex) {
                            Logger.getLogger(Valiuta.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(Valiuta.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ParserConfigurationException ex) {
                            Logger.getLogger(Valiuta.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        //pranešimas, jeigu datos nesuvestos teisingai
                        JDialog d = new JDialog(f, "dialog Box");
                        d.getContentPane().setLayout(new FlowLayout());
                        d.setSize(500, 100);
                        d.setVisible(true);
                        JLabel l = new JLabel("Blogai ivesti duomenys");
                        d.add(l);
                        ;
                    }
                } catch (ParseException ex) {
                    Logger.getLogger(Valiuta.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }
    //metodas, kuris duoda tinkamą užklausą į lb.lt ir xml atsakymą rašo į arraylist

    static void veiksmai(String nuo, String iki, String kodas) throws TransformerException, SAXException, IOException, ParserConfigurationException {

        //sukuriama užklausa
        String urlString = String.format("https://www.lb.lt/lt/currency/exportlist/?xml=1&currency=" + kodas + "&ff=1&class=Eu&type=day&date_from_day=" + nuo + "&date_to_day=" + iki);

        //užmezgamas ryšys
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        //padarom dokumentą iš gauto xml
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(conn.getInputStream());

        //išskiriam reikalingus atribiutus 
        NodeList code = doc.getElementsByTagName("valiutos_kodas");
        NodeList santykis = doc.getElementsByTagName("santykis");
        NodeList data = doc.getElementsByTagName("data");

        //įrašom reikiamus atribiutus į arraylist'ą
        for (int i = 0; i < code.getLength(); i++) {
            Element element = (Element) santykis.item(i);
            Element element1 = (Element) data.item(i);

            String santykis1 = element.getTextContent();
            String data1 = element1.getTextContent();

            valueList.add(santykis1);
            valueList.add(data1);
        }
    }
}
