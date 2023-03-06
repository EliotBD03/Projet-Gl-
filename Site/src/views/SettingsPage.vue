<template>
  <div class="main">
    <div class="header">
      <MainHeader text="Settings"/>
    </div>
    <div class="forms">
      <div class="formlanguage">
        <DropdownMain text="Choose your language..."/>
        <DropdownMain text="Add a language..."/>
        <GoButton text="Change language" :color="'#B1B9FC'"/>
      </div>
    </div>
    <div class="bottombutton">
      <GoButton text="Home" redirect="/" :color="'#B1B9FC'" expr="change"/>
    </div>
  </div>
</template>

<script>
import MainHeader from "@/components/MainHeader.vue";
import GoButton from "@/components/GoButton.vue";
import DropdownMain from "@/components/DropdownMain.vue";
export default {
  components: {
    DropdownMain,
    MainHeader,
    GoButton,
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

.bottombutton {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  padding: 0 50px;
  margin-top: 50px;
}

.formlanguage {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  width: 500px;
  height: 500px;
  border-radius: 50px;
  background: #e0e0e0;
  box-shadow: 20px 20px 60px #bebebe,
  -20px -20px 60px #ffffff;
}

.forms {
  display: flex;
  align-items: center;
  justify-content: space-evenly;
}
</style>