package org.is.html;

import javax.swing.*;
import javax.swing.text.*;
import java.io.*;
import java.util.*;

public class ModelDisplay {

	public static final int CONTENT_COLUMNS = 40;

	public static String showModel(Document doc){

		StringWriter sw = new StringWriter();
		BufferedWriter w = new BufferedWriter(sw);
		try {
			w.write("Document contains " + doc.getLength() + " characters\n");

			Element root = doc.getDefaultRootElement();
			showElement(root, w, 0);

			w.flush();
			return sw.toString();
		} catch (IOException e) {
			return e.toString();
		} catch (BadLocationException e) {
			return e.toString();
		}
	}

	protected static void showElement(Element e, Writer w, int indent) throws IOException, BadLocationException {

		String name = e.getName();
		int startOffset = e.getStartOffset();
		int endOffset = e.getEndOffset();

		doIndent(w, indent);
		w.write("<" + name + "> (" + startOffset + "," + endOffset + ") ");

		// Display the attributes, if there are any...
		w.write(HTMLUtils.attr2String(e.getAttributes()));
    w.write("\n");

		// If it is content, show some of the data
		if (name.equals(AbstractDocument.ContentElementName)) {
			// Display no more than CONTENT_COLUMNS characters
			int length = endOffset - startOffset;
			if (length > CONTENT_COLUMNS) {
				length = CONTENT_COLUMNS;
			}

			if (length > 0) {
				Segment s = new Segment();
				e.getDocument().getText(startOffset, length, s);
				doIndent(w, indent + 4);
				for (int i = 0; i < length; i++) {
					char c = s.array[i + s.offset];
					if (c == '\n') {
						w.write(" \\n ");
					} else if (c == '\r') {
						w.write(" \\r ");
					} else {
						w.write(s.array[i + s.offset]);
					}
				}
			}
		}
		w.write("\n");

		int children = e.getElementCount();
		for (int i = 0; i < children; i++) {
			showElement(e.getElement(i), w, indent + 4);
		}
		w.write("\n");    
		doIndent(w, indent);
		w.write("</" + name + ">\n");
	}

	protected static void doIndent(Writer w, int n)throws IOException {

		for (int i = 0; i < n; i++)w.write(" ");
	}

}
