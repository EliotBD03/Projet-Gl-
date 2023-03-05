<template>
  <div>
    <div>
      <input type="checkbox" :id="name" :value="name" v-model="listCheck">
      <label :for="name">{{ name }}</label>
    </div>
  </div>
</template>

<script>
export default {
  name: "checkButton",
  props: ["name"],
  data(){
    return{
      listCheck:[]
    }
  },
  methods:{
    getListCheck(){
      return this.listCheck;
    }
  }
}
</script>