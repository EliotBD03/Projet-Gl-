<template>
  <div class="main">
    <div class="header">
      <MainHeader text="New contracts"/>
    </div>
    <div class="container">
      <div class="list">
        <SelectListContract/>
      </div>
      <GoButton text="Home" redirect="/" :color="'#B1B9FC'" expr="change"/>
  </div>
  </div>
</template>
<script>
import MainHeader from "@/components/MainHeader.vue";
import GoButton from "@/components/GoButton.vue";
import SelectListContract from "@/components/SelectListContract.vue";
export default {
  components: {
    MainHeader,
    GoButton,
    SelectListContract,
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

.header {
  display: flex;
  align-items: center;
  justify-content: center;
}


.container {
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
}

.list {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>