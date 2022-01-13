/**
 * Call checkChanges method of this class when you will need to determine
 * did any form field changes occur since the most recent checkChange call
 * DO NOT SUIT FOR FieldInput !!!!
 * @example
 * constructor(props){
 *  super(props)
 * 
 *  ...
 * }
 * ...
 * loadData(){
 *      ...
 *      this.state.data = result
 *      this.comparator = new FieldsComparator(this) //component this should has this.state.data
 *      ...
 * }
 * ...
 * componentDidUpdate(){
 *  const fld = this.comparator.checkChanges()
 *  if(fld.lenght>0 && fld == "nameOfTextField"){
 *      this.save(); 
 *  }
 * }
 * 
 */
class FieldsComparator{
    /**
     * 
     * @param {always "this"} component - mandatory
     * @param {name of map (string)} map in component.state.data that contains pairs key,values. Example is "literals"
     * @example
     * new FieldComparator(this)
     * @example
     * new FieldComparator(this,"literals")
     */
    constructor(component,map){
        this.data = component.state.data
        if(map != undefined){
            this.data=component.state.data[map]
        }
        if(this.data != undefined){
            this.copy = this.cloneIt(this.data)
        }else{
            this.data={}
            this.copy={}
        }

        this.cloneIt=this.cloneIt.bind(this)
        this.checkChanges=this.checkChanges.bind(this)
        this.compareFldValues=this.compareFldValues.bind(this)
    }

    /**
     * Names of fields that have been changed since the last creation of this object
     * @returns field name or empty string if no changes
     */
    checkChanges(){
        let ret=[]
        for(let key in this.copy){
            if(this.copy[key] != undefined && this.copy[key].value != undefined){
                if(!this.compareFldValues(this.copy[key].value,this.data[key].value)){
                    ret.push(key)
                }
            }
        }
        return ret
    }

    /**
     * Compare field's values. Typically copy and original ones
     * @param {obj} copyVal - copy of a field's property value
     * @param {obj} origVal - original of a field's property value
     */
    compareFldValues(copyVal, origVal){
        if(copyVal.id == undefined){
            return copyVal==origVal
        }else{
            return copyVal.id == origVal.id
        }
    }

    /**
     * Simple and unefficient way to make a deep clone
     * @param {object} obj 
     */
    cloneIt(obj){
        const s = JSON.stringify(obj)
        return JSON.parse(s)
    }
}
export default FieldsComparator