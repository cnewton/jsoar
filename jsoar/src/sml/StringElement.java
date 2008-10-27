/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.35
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package sml;

public class StringElement extends WMElement {
  private long swigCPtr;

  protected StringElement(long cPtr, boolean cMemoryOwn) {
    super(smlJNI.SWIGStringElementUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(StringElement obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public synchronized void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      throw new UnsupportedOperationException("C++ destructor does not have public access");
    }
    swigCPtr = 0;
    super.delete();
  }

  public String GetValueType() {
    return smlJNI.StringElement_GetValueType(swigCPtr, this);
  }

  public String GetValueAsString() {
    return smlJNI.StringElement_GetValueAsString(swigCPtr, this);
  }

  public String GetValue() {
    return smlJNI.StringElement_GetValue(swigCPtr, this);
  }

  public StringElement ConvertToStringElement() {
    long cPtr = smlJNI.StringElement_ConvertToStringElement(swigCPtr, this);
    return (cPtr == 0) ? null : new StringElement(cPtr, false);
  }

}