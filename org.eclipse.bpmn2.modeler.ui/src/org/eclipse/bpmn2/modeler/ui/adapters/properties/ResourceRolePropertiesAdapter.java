/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 *  All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 *
 * @author Bob Brodt
 ******************************************************************************/

package org.eclipse.bpmn2.modeler.ui.adapters.properties;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ResourceAssignmentExpression;
import org.eclipse.bpmn2.ResourceRole;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.adapters.AdapterUtil;
import org.eclipse.bpmn2.modeler.ui.adapters.Bpmn2FeatureDescriptor;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * @author Bob Brodt
 *
 */
public class ResourceRolePropertiesAdapter extends Bpmn2ExtendedPropertiesAdapter {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public ResourceRolePropertiesAdapter(AdapterFactory adapterFactory, EObject object) {
		super(adapterFactory, object);
		// ResourceRoles are contained in Process, GlobalTask and Activity
    	setProperty(Bpmn2Package.RESOURCE_ROLE__RESOURCE_ASSIGNMENT_EXPRESSION, Bpmn2ExtendedPropertiesAdapter.UI_IS_MULTI_CHOICE, Boolean.FALSE);

    	final EStructuralFeature ref = Bpmn2Package.eINSTANCE.getResourceRole_ResourceAssignmentExpression();
    	setFeatureDescriptor(ref,
			new Bpmn2FeatureDescriptor(adapterFactory,object,ref) {

				@Override
				public String getText(Object context) {
					final ResourceRole rr = (ResourceRole)(context instanceof ResourceRole ? context : object);
					ResourceAssignmentExpression rae = rr.getResourceAssignmentExpression();
					if (rae!=null) {
						Bpmn2ExtendedPropertiesAdapter adapter = (Bpmn2ExtendedPropertiesAdapter) AdapterUtil.adapt(rae, Bpmn2ExtendedPropertiesAdapter.class);
						return adapter.getObjectDescriptor().getText(rae);
					}
					return "";
				}

				@Override
				public void setValue(EObject context, Object value) {
					final ResourceRole rr = (ResourceRole)(context instanceof ResourceRole ? context : object);
					ResourceAssignmentExpression rae = rr.getResourceAssignmentExpression();
					if (rae!=null) {
						Bpmn2ExtendedPropertiesAdapter adapter = (Bpmn2ExtendedPropertiesAdapter) AdapterUtil.adapt(rae, Bpmn2ExtendedPropertiesAdapter.class);
				    	EStructuralFeature raeFeature = Bpmn2Package.eINSTANCE.getResourceAssignmentExpression_Expression();
						adapter.getFeatureDescriptor(raeFeature).setValue(rae, value);
					}
				}
    		}
    	);
	}

}