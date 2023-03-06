<template>
  <div class="main">
    <ul class="list">
      <li class="list__item">
        <label class="label--checkbox" v-for="element in elements" :key="element">
          <CheckBox/>
          {{  element }}
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
  height: 100vh;
}
.list {
  display: flex;
  flex-direction: row;
  justify-content: space-evenly;
  align-items: center;
}
.list__item {
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
  gap: 10px;
}
</style>