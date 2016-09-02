package com.example.UI;

import com.example.BuscarDomicilioService;
import com.vaadin.annotations.Title;
import com.vaadin.data.Property;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Title("Prueba Maps")
@SpringUI
public class MyVaadinUI extends UI {
    private static final Logger log = LoggerFactory.getLogger(MyVaadinUI.class);

    private TextField direccion =  new TextField();
    private ComboBox cmbProvincias;
    private ListSelect listAddress = new ListSelect("Seleccione una direccion");
    private TextArea detalleDireccion = new TextArea("Detalle");


    @Autowired
    private BuscarDomicilioService buscarDomicilioService;

    @Override
    protected void init(VaadinRequest vaadinRequest) {



        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidth(400,Unit.PIXELS);
        mainLayout.addComponent(new Label("Ingrese una dirección"));


        Button btnConvertir = new Button("Buscar");
        btnConvertir.addClickListener(btnConvertirClickListener);
        cmbProvincias = new ComboBox("Seleccione una provincia", cargarProvincias());

        direccion.setWidth(200,Unit.PIXELS);

        listAddress.setRows(20);
        listAddress.setImmediate(true);
        listAddress.setWidth(100.0f, Unit.PERCENTAGE);
        listAddress.addValueChangeListener(listAddressValueChangeListener);

        detalleDireccion.setWidth(100.0f,Unit.PERCENTAGE);
        detalleDireccion.setRows(20);

        mainLayout.addComponent(direccion);
        mainLayout.addComponent(cmbProvincias);
        mainLayout.addComponent(btnConvertir);
        mainLayout.addComponent(listAddress);
        mainLayout.addComponent(detalleDireccion);
        setContent(mainLayout);


    }

    private List<String> cargarProvincias() {
        List<String> list = new ArrayList<String>();
        list.add("CABA");
        list.add("Buenos Aires");
        list.add("Neuquén");
        return list;
    }

    private Button.ClickListener btnConvertirClickListener = new Button.ClickListener() {
        @Override
        public void buttonClick(Button.ClickEvent clickEvent) {
            listAddress.removeAllItems();
            List<Map<String, Object>> addresses = buscarDomicilioService.buscarPorProvincia(direccion.getValue(),(String)cmbProvincias.getValue());

            for (Map<String, Object> address : addresses) {
                listAddress.addItem(address);
                listAddress.setItemCaption(address, (String)address.get("formatted_address"));
            }
        }
    };

    private Property.ValueChangeListener listAddressValueChangeListener= new Property.ValueChangeListener() {
        @Override
        public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
            if(listAddress.getValue() != null) {
                List<String> result = new ArrayList<String>();

                Map<String, Object> value = (Map<String, Object>) listAddress.getValue();
                List<Map> address_components = (List<Map>) value.get("address_components");
                for (Map component : address_components) {
                    List<String> types = (List) component.get("types");
                    result.add("[" + StringUtil.join(types, " - ") + " - " + (String) component.get("long_name") + "]");
                }
                detalleDireccion.setValue(StringUtil.join(result, ", "));
            } else{
                detalleDireccion.setValue("");
            }

        }
    };

}