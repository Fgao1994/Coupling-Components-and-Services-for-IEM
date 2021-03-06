/*
    Copyright (c) 2005,2006,2007, OpenMI Association
    "http://www.openmi.org/"

    This file is part of org.openmi.standard.jar

    org.openmi.standard.jar is free software; you can redistribute it and/or
    modify it under the terms of the Lesser GNU General Public License as
    published by the Free Software Foundation; either version 3 of the License,
    or (at your option) any later version.

    org.openmi.standard.jar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    Lesser GNU General Public License for more details.

    You should have received a copy of the Lesser GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.openmi.standard;

/**
 * The ILinkableComponent is the key interface in the OpenMI standard. Any
 * OpenMI compliant component must implement ILinkableComponent.
 * <p/>
 * <p/>OpenMI compliance definition:
 * <p/>
 * <p/>1) An OpenMI compliant component must implement the ILinkableComponent
 * interface according to specifications provided as comments in the
 * org.openmi.standard interface source code.
 * <p/>
 * <p/>2) An OpenMI compliant component must, when compiled, reference the
 * org.openmi.standard.jar, which is released and compiled by the OpenMI
 * Association.
 * <p/>
 * <p/>3) An OpenMI compliant component must be associated with a XML file,
 * which complies to (can be validated with) the LinkableComponent.xsd schema.
 * <p/>
 * <p/>4) An OpenMI compliant component must be associated with a XML file,
 * which complies to (can be validated with) the OpenMICompliancyInfo.xsd
 * schema. This file must be submitted to the OpenMI Association.
 * <p/>
 * <p/>5) The OpenMI Association provides two additional interfaces which
 * OpenMI compliant components may or may not implement; the IManageState
 * interface and the IDiscreteTimes interface. However, if these interfaces are
 * implemented, each method and property must implemented according to the
 * comments given in the org.openmi.standard interface source code.
 * <p/>
 * <p/>� 6) The OpenMI Association�s downloadable standard zip file provides the
 * only recognized version of source files, xml schemas and assembly file.
 */

public interface ILinkableComponent extends IPublisher {
    /**
     * Initializes the LinkableComponent.
     * <p/>The Initialize method will and must be invoked before any other
     * method or property in the ILinkableComponent interface is invoked.
     * <p/>When the Initialize methods has been invoked the properties ModelID,
     * ModelDescription, ComponentID, ComponentDescription,
     * InputExchangeItemCount, OutExchangeItemCount, TimeHorizon, and the
     * methods GetInputExchangeItem( ), GetOutputExchangeItem( ), AddLink( ),
     * RemoveLink( ), Validate( ), and Prepare( ) must be prepared for
     * invocation.
     * <p/>It is only required that the method Initialize can be invoked once.
     * If the Initialize method is invoked more that once and the
     * LinkableComponent cannot handle this; an exception must be thrown.
     * <p/>REMARKS: <p/>The properties argument is typically generated based on
     * the information given in the OMI file. The arguments typically contain
     * information about name and location of input files. <p/>The Initialize
     * method will typically populate the component by reading input files,
     * allocate memory, and organize the input exchange items and output
     * exchange items.
     *
     * @param properties see OpenMI.Standard.IArgument interface .
     */
    void initialize(IArgument[] properties);


    /**
     * Identifies the specific ILinkableComponent implementation (the class not
     * the instance/Object)
     * <p/>This property must be accessible after the Initialize( ) method has
     * been invoked and until the Finish( ) method has been invoked. If this
     * property is accessed before the Initialize( ) method has been invoked or
     * after the Finish( ) method has been invoked and the LinkableComponent
     * cannot handle this and exception must be thrown.
     * <p/>EXAMPLE: <p/>"MODFLOW", "Mike 11", "Hydroworks RS", "Sobek", "HEC
     * RAS"
     */
    String getComponentID();


    /**
     * Describes the ILinkableComponent implementation (the class not the
     * instance/Object)
     * <p/>This property must be accessible after the Initialize( ) method has
     * been invoked and until the Finish( ) method has been invoked. If this
     * property is accessed before the Initialize( ) method has been invoked or
     * after the Finish( ) method has been invoked and the LinkableComponent
     * cannot handle this an exception must be thrown.
     * <p/>EXAMPLE: <p/>"MODFLOW Ground water model", "Mike 11 riveer model",
     * "Hydroworks RS river model", "Sobek river model"
     */
    String getComponentDescription();

    /**
     * Identifies the instance of the LinkableComponent (the instantiated and
     * populated Object)
     * <p/>This property must be accessible after the Initialize( ) method has
     * been invoked and until the Finish( ) method has been invoked. If this
     * property is accessed before the Initialize( ) method has been invoked or
     * after the Finish( ) method has been invoked and the LinkableComponent
     * cannot handle this and exception must be thrown.
     * <p/>EXAMPLE: <p/>"The Rhine river model", "Catchment 23A"
     */
    String getModelID();


    /**
     * Describes the instance of the LinkableComponent (the instantiated and
     * populated Object)
     * <p/>This property must be accessible after the Initialize( ) method has
     * been invoked and until the Finish( ) method has been invoked. If this
     * property is accessed before the Initialize( ) method has been invoked or
     * after the Finish( ) method has been invoked and the LinkableComponent
     * cannot handle this and exception must be thrown.
     * <p/>EXAMPLE: <p/>"The Rhine river model hydrodynamic model, wet season
     * scenario"
     */
    String getModelDescription();

    /**
     * Defines the number of input exchange items that can be retrieves with the
     * GetInputExchangeItem( ) method.
     * <p/>This property must be accessible after the Initialize( ) method has
     * been invoked and until the Prepare( ) method has been invoked. If this
     * property is accessed before the Initialize( ) method has been invoked or
     * after the Prepare( ) method has been invoked and the LinkableComponent
     * cannot handle this an exception must be thrown.
     */
    int getInputExchangeItemCount();

    /**
     * Returns the InputExchangeItem corresponding to the inputExchangeItemIndex
     * provided in the method arguments.
     * <p/>This method must be accessible after the Initialize( ) method has
     * been invoked and until the Prepare( ) method has been invoked. If this
     * property is accessed before the Initialize( ) method has been invoked or
     * after the Prepare( ) method has been invoked and the LinkableComponent
     * cannot handle this an exception must be thrown.
     * <p/>This method basically returns a reference to the InputExchangeItem
     * Object. There is no guarantee that this Object is not altered by other
     * components after it has been returned. It is the responsibility of the
     * LinkableComponent to make sure that such possible alterations does not
     * subsequently corrupt the LinkableComponent.
     *
     * @param inputExchangeItemIndex Identifies the index-number of the
     *                               requested InputExchangeItem (indexing
     *                               starts from zero) This method must accept
     *                               values of inputExchangeItemIndex in the
     *                               interval [0, InputExchangeItemCount - 1].
     *                               If the inputExchangeItemIndex is outside
     *                               this interval an exception must be thrown.
     * @return The InputExchangeItem as identified by inputExchangeItemIndex.
     */
    IInputExchangeItem getInputExchangeItem(int inputExchangeItemIndex);

    /**
     * Defines the number of output exchange items that can be retrieves from
     * the GetOutputExchangeItem( ) method.
     * <p/>This property must be accessible after the Initialize( ) method has
     * been invoked and until the Prepare( ) method has been invoked. If this
     * property is accessed before the Initialize( ) method has been invoked or
     * after the Prepare( ) method has been invoked and the LinkableComponent
     * cannot handle this an exception must be thrown.
     */
    int getOutputExchangeItemCount();

    /**
     * Returns the OutputExchangeItem corresponding to the
     * outputExchangeItemIndex provided in the method arguments.
     * <p/>This method must be accessible after the Initialize( ) method has
     * been invoked and until the Prepare( ) method has been invoked. If this
     * property is accessed before the Initialize( ) method has been invoked or
     * after the Prepare( ) method has been invoked and the LinkableComponent
     * cannot handle this an exception must be thrown.
     * <p/>This method basically returns a reference to the OutputExchangeItem
     * Object. There is no guarantee that this Object is not altered by other
     * components after it has been returned. It is the responsibility of the
     * LinkableComponent to make sure that such possible alterations does not
     * subsequently corrupt the LinkableComponent.
     *
     * @param outputExchangeItemIndex Identifies the index-number of the
     *                                requested OutputExchangeItem (indexing
     *                                starts from zero) This method must accept
     *                                values of outputExchangeItemIndex in the
     *                                interval [0, OutputExchangeItemCount - 1].
     *                                If the outputExchangeItemIndex is outside
     *                                this interval an exception must be
     *                                thrown.
     * @return The OutputExchangeItem as identified by inputExchangeItemIndex.
     */
    IOutputExchangeItem getOutputExchangeItem(int outputExchangeItemIndex);

    /**
     * Defines the time span within which the LinkableComponent can return
     * values without using temporal extrapolation. For numerical models this is
     * typically the time horizon of the available input data. If the
     * LinkableComponent does not know time at all or of the LinkableComponent
     * can provide data at any time null should be returned.
     * <p/>Note that the GetValues( ) method may be invoked with time arguments
     * that outside the time horizon defined in this method. In this case the
     * linkable component may return extrapolated values or throw an exception
     * if the sound values cannot be generated by means of extrapolation.
     * <p/>This property must be accessible after the Initialize( ) method has
     * been invoked and until the Prepare( ) method has been invoked. If this
     * property is accessed before the Initialize( ) method has been invoked or
     * after the Prepare( ) method has been invoked and the LinkableComponent
     * cannot handle this an exception must be thrown.
     */
    ITimeSpan getTimeHorizon();

    /**
     * Adds a Link to the LinkableComponent
     * <p/>This method must be accessible after the Initialize( ) method has
     * been invoked and until the Prepare( ) method has been invoked. If this
     * property is accessed before the Initialize( ) method has been invoked or
     * after the Prepare method has been invoked and the LinkableComponent
     * cannot handle this an exception must be thrown.
     *
     * @param link Link to be added.
     */
    void addLink(ILink link);

    /**
     * Removes a Link from the LinkableComponent
     * <p/>This method must be accessible after the Initialize( ) method has
     * been invoked and until the Prepare( ) method has been invoked. If this
     * property is accessed before the Initialize( ) method has been invoked or
     * after the Prepare( ) method has been invoked and the LinkableComponent
     * cannot handle this an exception must be thrown.
     * <p/>If the LinkID is not recognized an exception must be thrown
     *
     * @param linkID LinkID for the link to be removed.
     */
    void removeLink(String linkID);

    /**
     * Validates the populated instance of the LinkableComponent
     * <p/>This method must be accessible after the Initialize( ) method has
     * been invoked and until the Finish( ) method has been invoked. If this
     * property is accessed before the Initialize( ) method has been invoked or
     * after the Finish( ) method has been invoked and the LinkableComponent
     * cannot handle this an exception must be thrown.
     *
     * @return Returns an empty String if the component is valid otherwise
     *         returns a message
     */
    String validate();

    /**
     * Prepare for GetValues invocation
     * <p/>This method must be accessible after the Initialize( ) method has
     * been invoked and until the Finish( ) method has been invoked. If this
     * property is accessed before the Initialize( ) method has been invoked or
     * after the Finish( ) method has been invoked and the LinkableComponent
     * cannot handle this an exception must be thrown.
     * <p/>It is only required that the Prepare( ) method can be invoked once.
     * If the Prepare method is invoked more that once and the LinkableComponent
     * cannot handle this an exception must be thrown.
     */
    void prepare();

    /**
     * Returns a ValuesSet, which is either a ScalarSet or a VectorSet, where
     * the values applies to the time (TimeStamp or TimeSpan) defined in the
     * method arguments and corresponds to the previously added Link, which is
     * identified by the LinkID provided in the method arguments.
     * <p/>The number of Scalars or Vectors must match the number of elements in
     * the target ElementSet as defined in Link.TargetElementSet.ElementCount
     * <p/>The GetValues method must accept requests for data for any previously
     * added link. If the LinkID is not recognized an exception must be thrown.
     * <p/>If the Unit conversion factor and/or the Unit offset of the provided
     * quantity does not match the Unit conversion factor and/or the Unit offset
     * of the accepting quantity, unit conversion must be performed so the
     * provided values corresponds to the unit of the accepting quantity.
     * <p/>If the accepting ElementSet is geo-referenced the provided values
     * must be converted to apply to the geometry of the elements in the
     * accepting ElementSet.
     * <p/>If the LinkableComponent at the time when the GetValues method is
     * invoked has invoked the GetValues method on the invoking LinkableComponet
     * and this component has not yet return the values, the LinkableComponent
     * may not re-invoke the GetValues method again on that component until it
     * has returned the values.
     * <p/>The GetValues method is not required to return values for times
     * outside the TimeHorizon of the LinkableComponent. If the GetValues method
     * is invoked with a time argument that is outside the TimeHorizon of the
     * LinkableComponent and the LinkableComponent cannot handle such invocation
     * an exception must be thrown.
     * <p/>The GetValues method is not required to return values before the time
     * defined by the EarliestInputTime property of the accepting component. If
     * the GetValues method is invoked with a time argument that is before this
     * time and the LinkableComponent cannot handle this an exception must be
     * thrown
     * <p/>The LinkableComponent must send the SourceAfterGetValuesCall event
     * immediately after the GetValues method is invoked. The LinkableComponent
     * must send the SourceBeforeGetValuesReturn event immediately preceding it
     * returns the values. If the LinkableComponent invokes GetValues in an
     * other LinkableComponents it must send the TargetBeforeGetValuesCall event
     * immediately preceding the GetValues invocation and the
     * TargetAfterGetValuesReturn event immediately after the invoked
     * LinkableComponent has retuned the values. If the LinkableComponent is
     * progressing time steps or changing state the TimeStepProgress event
     * and/or the DataChange event, respectively must be send.
     * <p/>The GetValues method must be accessible after the Prepare( ) method
     * has been invoked and until the Finish( ) method has been invoked. If this
     * Method is accessed before the Prepare( ) method has been invoked or after
     * the Finish( ) method has been invoked and the LinkableComponent cannot
     * handle this an exception must be thrown.
     * <p/>This method basically returns a reference to the ValueSet Object.
     * There is no guarantee that this Object is not altered by other components
     * after it has been returned. It is the responsibility of the
     * LinkableComponent to make sure that such possible alterations does not
     * subsequently corrupt the LinkableComponent.
     *
     * @param time   The time for which the values are requested.
     * @param linkID ID for the previously added link Object.
     * @return ValueSet corresponding the the time and LinkID argument
     *         (ScalarSet or VectorSet)
     */
    IValueSet getValues(ITime time, String linkID);

    /**
     * The earliestInputTime property defines earliest time for which the
     * LinkableComponent will invoke the GetValues on other LinkableComponent.
     * The LinkableComponent must ensure that earliestInputTime property always
     * reflects the latest possible time. When the earliestInputTime property is
     * updated the updated time must always be later than any previous times
     * exposed by this property
     * <p/>This property must be accessible after the Prepare( ) method has been
     * invoked and until the Finish( ) method has been invoked. If this property
     * is accessed before the Prepare( ) method has been invoked or after the
     * Finish( ) method has been invoked and the LinkableComponent cannot handle
     * this an exception must be thrown.
     * <p/>REMARKS: <p/>Most time stepping numerical models keeps typically only
     * the calculated values corresponding to the current time step and the
     * previous time step in memery. In order to enable the LinkableComponent to
     * return values before that an internal buffer is typically established for
     * each link added to this component. The earliestInputTime property on the
     * LinkableComponent to which such component is linked is typically used to
     * detect which time related data that can be removed from these buffers.
     */
    ITimeStamp getEarliestInputTime();

    /**
     * This method is and must be invoked as the second last of any methods in the
     * ILinkableComponent interface.
     * <p/>This method must be accessible after the Initialize( ) method has been
     * invoked and before the Dispose( ) method has been invoked. If this method
     * is invoked before the Initialize( ) method has been invoked or after the
     * Dispose( ) methods has been invoked and the LinkableComponent cannot
     * handled this an exception must be thrown.
     */
    void finish();

    /**
     * This method is and must be invoked as the last of any methods in the
     * ILinkableComponent interface.
     * <p/>This method must be accessible after the Finish( ) method has
     * been invoked. If this method is invoked before the Finish( ) method
     * has been invoked and the LinkableComponent cannot handled this an
     * exception must be thrown.
     */
    void dispose();

}