import Fetchers from './Fetchers'

/**
 * Works with i18N on the server
 * @example
 * Locales.createLabels(this) - create list of empty this.state.labels from name of properties in this.state.data
 * @example
 * Locales.resolveLabels(this) - call the server to resolve this.state.labels to cyrrent language values
 */
class Locales{

    /**
     * add keys of component.state.data 
     * Recursive
     * @param {object} component implementation of the component. Must implement "this.state.data"
     * @param {string} literal - field with literals, not mandatory
     * @example
     * Locales.createLabels(this, "literals")
     */
    static createLabels(component,literal){
        if(component.state.labels==undefined){
            component.state.labels={}
        }
        Locales.createLabelsRecursive(component.state.data, component.state.labels, literal);
    }

    /**
     * collect labels for all fields in a functional component data
     * @param {data} - data that may contain fields
     * @param {labels} a map. Key is label, value is FieldDTO
     * @returns a list of field's labels
     */
    static createLabelsFunctional(data,labels){
        if(data != undefined && Fetchers.isGoodArray(Object.keys(data))){
            let newLabels={}
            Object.keys(data).forEach((key)=>{
                newLabels[key]=''
            })
            return {...labels,...newLabels}
        }else{
            return {...labels}
        }
    }

    static createLabelsRecursive(data, labels,literal){
        if(data != undefined && Fetchers.isGoodArray(Object.keys(data))){
            Object.keys(data).forEach((key)=>{
                if(key == literal){
                    Locales.createLabelsRecursive(data[key],labels)
                }else{
                    labels[key]=''
                }
            })
        }
    }

    /**
     * Resolve labels in functional components
     */
    static resolveLabelsFunctional(labels, setLabels){
        let component ={
            state:{
                labels:Object.assign({}, labels),
            }
        }
        Locales.resolveLabels(component, setLabels)
        
    }

    /**
     * Load labels with the current locale for a component
     * @param {object} implementation of the component. Must implement "this.state.labels"
     * @param {setLabels} - useState function for functional components optional
     * @example  Locales.resolveLabels(this) 
     */
    static resolveLabels(component, setLabels){
        //labels should be loaded
        let toLoad=[] 
        //labels that are already loaded
        if(typeof window.etbm_labels == 'undefined'){
            window.etbm_labels={}
        }
        let loaded = window.etbm_labels; 
        // is an object in the parameter correct? 
        if(typeof component != 'undefined' && typeof component.state != 'undefined' && typeof component.state.labels != 'undefined'){
            //current component's labels
            let componentLabels = component.state.labels
            //determine which keys should be loaded
            for(let key in componentLabels){
                //console.log(key)
                if(typeof loaded[key] == 'undefined' || loaded[key] != component.state.labels[key]){
                    toLoad.push(key)
                }
            }

            //ask for the keys that should be loaded
            if(toLoad.length>0){
                Fetchers.postJSONNoSpinner('/api/public/provideLabels',toLoad,(query,result)=>{
                    let refresh=false
                    for(let key in result){
                        loaded[key]=result[key]
                    }

                    //determine does something change
                    for(let key in componentLabels){
                        if((loaded[key] != undefined)){
                            componentLabels[key] = loaded[key]
                            refresh=true
                        }
                    }
                    componentLabels['locale'] = loaded['locale']
                    //refresh component's state if somethig changes
                    if(refresh){
                        let s = component.state
                        if(setLabels==undefined){
                            component.setState(s)
                        }else{
                            setLabels(s.labels)
                        }
                    }
                })
            }
        }else{
            console.log("this.state.labels is undefined for some component")
        }

    }
    /**
     * Has labels loaded? 
     * @param component - any component, should provede this.state.labels
     * @example
     * Locales.isReady(this) 
     */
    static isReady(component){
        return component.state.labels.locale != undefined
    }
}

export default Locales
