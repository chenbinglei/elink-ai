import {defineCustomElement} from 'vue';
import EchartsComponent from './EchartsComponent.ce.vue';

// 注册自定义标签组件
const defineCustomElementWrapped = (component) => defineCustomElement(component);

const customElementArray = [
    {componentName: 'meta2d-echarts-component', component: EchartsComponent},
]

// 注册组件
for (let i = 0; i < customElementArray.length; i++) {
    if (!customElements.get(customElementArray[i].componentName)) {
        customElements.define(customElementArray[i].componentName, defineCustomElementWrapped(customElementArray[i].component));
    }
}
