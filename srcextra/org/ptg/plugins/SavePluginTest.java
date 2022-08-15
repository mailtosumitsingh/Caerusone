package org.ptg.plugins;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.ptg.plugins.DefaultDroppable;
import org.ptg.plugins.DefaultHandlerDef;
import org.ptg.plugins.DefaultPlugin;
import org.ptg.plugins.DefaultPluginMenu;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;


public class SavePluginTest {
	private void createDummyPlugin(String string) throws Exception {
		DefaultPlugin d = new DefaultPlugin();
		d.setId(string);
		DefaultDroppable drop1 = new DefaultDroppable();
		DefaultHandlerDef hdef = new DefaultHandlerDef();
		DefaultPluginMenu menu1 = new DefaultPluginMenu();
		drop1.setId("drop1");
		drop1.setCode("code1");
		hdef.setHandlerClass("handlerclass1");
		hdef.setPath("/path1");
		menu1.setAction("action1");
		menu1.setDisplayName("disp1");
		menu1.setWhereToAdd("where1");
		menu1.setId("menu1");
		d.getDropables().add(drop1);
		d.getMenus().add(menu1);
		d.getHandlers().add(hdef);
		d.getIncludes().add("include1.js");
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		JAXBContext context = JAXBContext.newInstance(DefaultPlugin.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
         marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
         marshaller.setProperty(CharacterEscapeHandler.class.getName(),
                 new CharacterEscapeHandler() {
                     @Override
                     public void escape(char[] ac, int i, int j, boolean flag,Writer writer) throws IOException {
                         writer.write(ac, i, j);
                     }
                 });	
            marshaller.marshal(d,bs);
			System.out.println(new String(bs.toByteArray()));

	}
}
