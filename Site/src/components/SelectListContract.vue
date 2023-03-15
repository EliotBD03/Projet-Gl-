<template>
  <div class="main">
    <ul class="list">
      <li class="list__item">
        <label class="label--checkbox" v-for="element in elements" :key="element">
          <CheckBox :text="element" />
        </label>
      </li>
    </ul>
  </div>
</template>
<script>
import CheckBox from "@/components/CheckBox.vue";

export default {
  name: "SelectListContract",
  components: {CheckBox},
  data() {
    return {
      elements: [
          "Electricity",
          "Gas",
          "Water",
          "Internet",
          "Phone",
          "TV",
          "Other"
      ]
    }
  }
};
</script>

<style scoped>
.main {
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
  align-items: center;
}

.list {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 150px;
  height: 450px;
  background: rgb(236, 236, 236);
  box-shadow: rgba(0, 0, 0, 0.4) 0px 2px 4px, rgba(0, 0, 0, 0.3) 0px 7px 13px -3px, rgba(0, 0, 0, 0.2) 0px -3px 0px inset;
  position: absolute;
  left: 0;
  margin: 10px;
}

.list__item {
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
  position: absolute;
  left: 0;
  margin: 10px;
  top: 10px;
}
</style>