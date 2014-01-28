/******************************************************************************* 
 * Copyright (c) 2011, 2012 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *
 * @author Innar Made
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.features.artifact;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.modeler.core.di.DIImport;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddElementFeature;
import org.eclipse.bpmn2.modeler.core.features.AbstractUpdateBaseElementFeature;
import org.eclipse.bpmn2.modeler.core.features.BaseElementFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.DefaultMoveBPMNShapeFeature;
import org.eclipse.bpmn2.modeler.core.features.artifact.AbstractCreateArtifactFeature;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.utils.StyleUtil;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.bpmn2.modeler.ui.features.AbstractDefaultDeleteFeature;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.graphiti.ui.services.GraphitiUi;

public class GroupFeatureContainer extends BaseElementFeatureContainer {
	protected final IGaService gaService = Graphiti.getGaService();
	protected final IPeService peService = Graphiti.getPeService();

	@Override
	public boolean canApplyTo(Object o) {
		return super.canApplyTo(o) && o instanceof Group;
	}

	@Override
	public ICreateFeature getCreateFeature(IFeatureProvider fp) {
		return new CreateGroupFeature(fp);
	}

	@Override
	public IAddFeature getAddFeature(IFeatureProvider fp) {
		return new AddGroupFeature(fp);
	}

	@Override
	public IDeleteFeature getDeleteFeature(IFeatureProvider fp) {
		return new DeleteGroupFeature(fp);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		return new UpdateGroupFeature(fp);
	}

	@Override
	public IDirectEditingFeature getDirectEditingFeature(IFeatureProvider fp) {
		return null;
	}

	@Override
	public ILayoutFeature getLayoutFeature(IFeatureProvider fp) {
		return null;
	}

	@Override
	public ICustomFeature[] getCustomFeatures(IFeatureProvider fp) {
		return null;
	}

	@Override
	public IMoveShapeFeature getMoveFeature(IFeatureProvider fp) {
		return new MoveGroupFeature(fp);
	}

	@Override
	public IResizeShapeFeature getResizeFeature(IFeatureProvider fp) {
		return new ResizeGroupFeature(fp);
	}

	public class AddGroupFeature extends AbstractBpmn2AddElementFeature<Group> {
		public AddGroupFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public boolean canAdd(IAddContext context) {
			return true;
		}

		@Override
		public PictogramElement add(IAddContext context) {
			IGaService gaService = Graphiti.getGaService();
			IPeService peService = Graphiti.getPeService();
			Group businessObject = getBusinessObject(context);

			int x = context.getX();
			int y = context.getY();
			int width = this.getWidth(context);
			int height = this.getHeight(context);
			
			if (!(context.getTargetContainer() instanceof Diagram)) {
				ILocation loc = Graphiti.getPeService().getLocationRelativeToDiagram(context.getTargetContainer());
				x += loc.getX();
				y += loc.getY();
				((AddContext)context).setTargetContainer(this.getDiagram());
			}

			ContainerShape containerShape = peService.createContainerShape(context.getTargetContainer(), true);
			link(containerShape, businessObject);
			
			// NOTE: We do not want a Group Shape to be a graphiti shape container for
			// anything that is added or moved into the Group, so instead of using a
			// rectangle for the Group shape, we'll use a polyline instead.
//			RoundedRectangle rect = gaService.createRoundedRectangle(containerShape, 5, 5);
//			rect.setFilled(false);
//			rect.setLineWidth(2);
//			rect.setForeground(manageColor(StyleUtil.CLASS_FOREGROUND));
//			rect.setLineStyle(LineStyle.DASHDOT);
//			gaService.setLocationAndSize(rect, x, y, width, height);
//			peService.createChopboxAnchor(containerShape);
//			AnchorUtil.addFixedPointAnchors(containerShape, rect);
			int xy[] = new int[] {0, 0, width, 0, width, height, 0, height, 0, 0};
			Polyline rect = gaService.createPolyline(containerShape, xy);
			rect.setLineWidth(2);
			rect.setForeground(manageColor(StyleUtil.CLASS_FOREGROUND));
			rect.setLineStyle(LineStyle.DASHDOT);
			gaService.setLocationAndSize(rect, x, y, width, height);
			peService.createChopboxAnchor(containerShape);
			AnchorUtil.addFixedPointAnchors(containerShape, rect);
//
			
			boolean isImport = context.getProperty(DIImport.IMPORT_PROPERTY) != null;
			createDIShape(containerShape, businessObject, !isImport);
			
			// hook for subclasses to inject extra code
			((AddContext)context).setWidth(width);
			((AddContext)context).setHeight(height);
			decorateShape(context, containerShape, businessObject);


			return containerShape;
		}

		@Override
		protected void decorateShape(IAddContext context, ContainerShape containerShape, Group businessObject) {
			Shape textShape = peService.createShape(containerShape, false);
			String name = "";
			if (businessObject.getCategoryValueRef()!=null)
				name = ModelUtil.getDisplayName(businessObject.getCategoryValueRef());
			MultiText text = gaService.createDefaultMultiText(getDiagram(), textShape, name);
			StyleUtil.applyStyle(text, businessObject);
			IDimension size = GraphitiUi.getUiLayoutService().calculateTextSize("My", text.getFont());
			gaService.setLocationAndSize(text, 0, 0, context.getWidth(), size.getHeight()+4);
			text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
			text.setVerticalAlignment(Orientation.ALIGNMENT_TOP);
			link(textShape, businessObject);
		}

		@Override
		public int getHeight() {
			return 200;
		}

		@Override
		public int getWidth() {
			return 200;
		}
	}

	public static class CreateGroupFeature extends AbstractCreateArtifactFeature<Group> {

		public CreateGroupFeature(IFeatureProvider fp) {
			super(fp, Messages.GroupFeatureContainer_Name, Messages.GroupFeatureContainer_Description);
		}

		@Override
		public String getStencilImageId() {
			return ImageProvider.IMG_16_GROUP;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateFeature#getBusinessObjectClass()
		 */
		@Override
		public EClass getBusinessObjectClass() {
			return Bpmn2Package.eINSTANCE.getGroup();
		}
	}
	
	public static class UpdateGroupFeature extends AbstractUpdateBaseElementFeature {

		public UpdateGroupFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public boolean canUpdate(IUpdateContext context) {
			return true;
		}

		@Override
		public boolean update(IUpdateContext context) {
			super.update(context);
			
			ContainerShape groupShape = (ContainerShape)context.getPictogramElement();
			for (ContainerShape cs : FeatureSupport.findGroupedShapes(groupShape)) {
				FeatureSupport.updateCategoryValues(getFeatureProvider(), cs);
			}
			return true;
		}
		
	}
	
	public static class MoveGroupFeature extends DefaultMoveBPMNShapeFeature {

		public MoveGroupFeature(IFeatureProvider fp) {
			super(fp);
		}
		List<ContainerShape> containedShapes = new ArrayList<ContainerShape>();

		@Override
		public boolean canMoveShape(IMoveShapeContext context) {
			return true;
		}

		@Override
		protected void preMoveShape(IMoveShapeContext context) {
			super.preMoveShape(context);
			ContainerShape groupShape = (ContainerShape) context.getShape();
			ContainerShape container = context.getTargetContainer();
			if (!(container instanceof Diagram)) {
				ILocation loc = Graphiti.getPeService().getLocationRelativeToDiagram(container);
				int x = context.getX() + loc.getX();
				int y = context.getY() + loc.getY();
				((MoveShapeContext)context).setX(x);
				((MoveShapeContext)context).setY(y);
				((MoveShapeContext)context).setDeltaX(x - preShapeX);
				((MoveShapeContext)context).setDeltaY(y - preShapeY);
				((MoveShapeContext)context).setTargetContainer(getDiagram());
			}

			// find all shapes that are inside this Group
			// these will be moved along with the Group
			containedShapes = FeatureSupport.findGroupedShapes(groupShape);
		}

		@Override
		protected void postMoveShape(IMoveShapeContext context) {

			super.postMoveShape(context);
			
			ContainerShape groupShape = (ContainerShape) context.getShape();
			for (ContainerShape shape : containedShapes) {
				if (!FeatureSupport.isLabelShape(shape)) {
					ILocation loc = Graphiti.getPeService().getLocationRelativeToDiagram(shape);
					int x = loc.getX() + context.getDeltaX();
					int y = loc.getY() + context.getDeltaY();
					MoveShapeContext mc = new MoveShapeContext(shape);
					mc.setSourceContainer(shape.getContainer());
					mc.setTargetContainer(shape.getContainer());
					mc.setX(x);
					mc.setY(y);
					IMoveShapeFeature mf = getFeatureProvider().getMoveShapeFeature(mc);
					mf.moveShape(mc);
				}
			}
			for (ContainerShape cs : FeatureSupport.findGroupedShapes(groupShape)) {
				if (!containedShapes.contains(cs)) {
					FeatureSupport.updateCategoryValues(getFeatureProvider(), cs);
					containedShapes.add(cs);
				}
			}
			// Update CategoryValues for SequenceFlows also
			List<Connection> connections = new ArrayList<Connection>();
			for (ContainerShape cs : containedShapes) {
				for (Anchor a : cs.getAnchors()) {
					for (Connection c : a.getIncomingConnections()) {
						if (!connections.contains(c))
							connections.add(c);
					}
					for (Connection c : a.getOutgoingConnections()) {
						if (!connections.contains(c))
							connections.add(c);
					}
				}
			}
			for (Connection c : connections) {
				FeatureSupport.updateCategoryValues(getFeatureProvider(), c);
			}
		}
	}

	public class ResizeGroupFeature extends DefaultResizeShapeFeature {

		public ResizeGroupFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public void resizeShape(IResizeShapeContext context) {
			ContainerShape groupShape = (ContainerShape) context.getPictogramElement();
			List<ContainerShape> containedShapesBeforeResize = FeatureSupport.findGroupedShapes(groupShape);

			int x = context.getX();
			int y = context.getY();
			int w = context.getWidth();
			int h = context.getHeight();
			Polyline rect = (Polyline) groupShape.getGraphicsAlgorithm();
			Point p;
			p = rect.getPoints().get(1);
			p.setX(w);
			p = rect.getPoints().get(2);
			p.setX(w);
			p.setY(h);
			p = rect.getPoints().get(3);
			p.setY(h);
			Graphiti.getGaService().setLocationAndSize(rect, x, y, w, h);
			for (Shape shape : groupShape.getChildren()) {
				if (shape.getGraphicsAlgorithm() instanceof AbstractText) {
					AbstractText text = (AbstractText) shape.getGraphicsAlgorithm();
					gaService.setLocationAndSize(text, 0, 0, w, text.getHeight());
				}
			}

			DIUtils.updateDIShape(context.getPictogramElement());

			List<ContainerShape> containedShapesAfterResize = FeatureSupport.findGroupedShapes(groupShape);
			for (ContainerShape cs : containedShapesBeforeResize) {
				if (!containedShapesAfterResize.contains(cs))
					FeatureSupport.updateCategoryValues(getFeatureProvider(), cs);
			}
			for (ContainerShape cs : containedShapesAfterResize) {
				if (!containedShapesBeforeResize.contains(cs))
					FeatureSupport.updateCategoryValues(getFeatureProvider(), cs);
			}
			
			FeatureSupport.updateConnections(getFeatureProvider(), groupShape);

			// Update CategoryValues for SequenceFlows also
			List<Connection> connections = new ArrayList<Connection>();
			for (ContainerShape cs : containedShapesBeforeResize) {
				for (Anchor a : cs.getAnchors()) {
					for (Connection c : a.getIncomingConnections()) {
						if (!connections.contains(c))
							connections.add(c);
					}
					for (Connection c : a.getOutgoingConnections()) {
						if (!connections.contains(c))
							connections.add(c);
					}
				}
			}
			for (ContainerShape cs : containedShapesAfterResize) {
				for (Anchor a : cs.getAnchors()) {
					for (Connection c : a.getIncomingConnections()) {
						if (!connections.contains(c))
							connections.add(c);
					}
					for (Connection c : a.getOutgoingConnections()) {
						if (!connections.contains(c))
							connections.add(c);
					}
				}
			}
			for (Connection c : connections) {
				FeatureSupport.updateCategoryValues(getFeatureProvider(), c);
			}
		}
	}
	
	public class DeleteGroupFeature extends AbstractDefaultDeleteFeature {

		public DeleteGroupFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public void delete(IDeleteContext context) {
			ContainerShape groupShape = (ContainerShape) context.getPictogramElement();
			List<ContainerShape> containedShapes = FeatureSupport.findGroupedShapes(groupShape);
			
			super.delete(context);

			for (ContainerShape cs : containedShapes) {
				FeatureSupport.updateCategoryValues(getFeatureProvider(), cs);
			}
		}
		
	}
}