package br.mil.eb.dsg.arctools;

import java.io.IOException;

import com.esri.arcgis.addins.desktop.Extension;
import com.esri.arcgis.arcmapui.IMxDocument;
import com.esri.arcgis.carto.esriViewDrawPhase;
import com.esri.arcgis.editor.Editor;
import com.esri.arcgis.editor.IEditEventsAdapter;
import com.esri.arcgis.editor.IEditEventsOnChangeFeatureEvent;
import com.esri.arcgis.editor.IEditEventsOnCreateFeatureEvent;
import com.esri.arcgis.framework.IApplication;
import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geometry.IGeometry;
import com.esri.arcgis.interop.AutomationException;
import com.esri.arcgis.system.IExtension;
import com.esri.arcgis.system.IUID;
import com.esri.arcgis.system.UID;

public class AutoCotar extends Extension {

	private Editor m_editor;
	public IMxDocument mxDoc;
	
	/**
	 * Initializes this application extension with the ArcMap application instance it is hosted in.
	 * 
	 * This method is automatically called by the host ArcMap application.
	 * It marks the start of the dockable window's lifecycle.
	 * Clients must not call this method.
	 * 
	 * @param app is a reference to ArcMap's IApplication interface
	 * @exception java.io.IOException if there are interop problems.
	 * @exception com.esri.arcgis.interop.AutomationException if the component throws an ArcObjects exception.
	 */
	@Override
	public void init(IApplication app) throws IOException, AutomationException {
		mxDoc = (IMxDocument) app.getDocument();

		IUID extensionID = new UID();
		extensionID.setValue("esriEditor.Editor");
		IExtension editExtension = app.findExtensionByCLSID(extensionID);
		m_editor = (Editor) editExtension;

		m_editor.addIEditEventsListener(new IEditEventsAdapter(){
			
			private static final long serialVersionUID = 1L;

			public void onCreateFeature(IEditEventsOnCreateFeatureEvent arg0) throws IOException, AutomationException {
				IFeature theFeature = (IFeature) arg0.getObj();
				int atribCota = theFeature.getFields().findField("cota");
				if(atribCota >= 0)
				{
					IGeometry theGeom = theFeature.getShape();
					theFeature.setValue(atribCota, theGeom.getEnvelope().getZMax());
					theFeature.store();
	
					mxDoc.getActiveView().partialRefresh(esriViewDrawPhase.esriViewGraphics, null, theFeature.getExtent());
				}
			}
			
			public void onChangeFeature(IEditEventsOnChangeFeatureEvent arg0) throws IOException, AutomationException {
				this.onCreateFeature(new IEditEventsOnCreateFeatureEvent(arg0.getObj()));
			}
		});
	}

}
