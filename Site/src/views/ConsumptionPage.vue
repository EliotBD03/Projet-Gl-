<template>
  <div class="main">
    <div class="header">
      <MainHeader text="Consumptions"/>
    </div>
    <div class="topbutton">
      <GoButton text="Table" :colore="'green'"/>
      <GoButton text="Export" :colore="'green'"/>
      <GoButton text="Graphic" :colore="'green'"/>
    </div>
    <div class="infos">
      <div class="container">
      </div>
      <div class="newconsumption">
      <p>New consumption : 12/02/2024</p>
        <GoButton text="Add" :colore="'green'"/>
      </div>
      <div class="changeconsumption">
        <p>Change consumption : 12/02/2024</p>
        <GoButton text="Change" :colore="'green'"/>
      </div>
    </div>
    <div class="bottombutton">
      <div class="returnbutton" @click.prevent.left="$router.push('/wallets')">
      <GoButton text="Back" :colore="'darkblue'"/>
      </div>
      <div class="closebutton">
      <GoButton text="Close the wallet" :colore="'red'"/>
        </div>
    </div>
  </div>
</template>

<script>
import GoButton from "@/components/GoButton.vue";
import MainHeader from "@/components/MainHeader.vue";

export default {
  components: {
    GoButton,
    MainHeader
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

.topbutton {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  padding: 0 50px;
  margin-top: 50px;
}

.infos {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-evenly;
  padding: 0 50px;
  margin-top: 50px;
}

.container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 1000px;
  height: 400px;
  background: rgb(236, 236, 236);
  box-shadow: rgba(0, 0, 0, 0.4) 0px 2px 4px, rgba(0, 0, 0, 0.3) 0px 7px 13px -3px, rgba(0, 0, 0, 0.2) 0px -3px 0px inset;
}

.newconsumption {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-evenly;
  width: 1000px;
  height: 70px;
  background: rgb(236, 236, 236);
  box-shadow: rgba(0, 0, 0, 0.4) 0px 2px 4px, rgba(0, 0, 0, 0.3) 0px 7px 13px -3px, rgba(0, 0, 0, 0.2) 0px -3px 0px inset;
  margin: 10px;
  font-family: Avenir, Helvetica, Arial, sans-serif;
  font-size: 30px;
}

.changeconsumption {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-evenly;
  width: 1000px;
  height: 70px;
  background: rgb(236, 236, 236);
  box-shadow: rgba(0, 0, 0, 0.4) 0px 2px 4px, rgba(0, 0, 0, 0.3) 0px 7px 13px -3px, rgba(0, 0, 0, 0.2) 0px -3px 0px inset;
  margin: 10px;
  font-family: Avenir, Helvetica, Arial, sans-serif;
  font-size: 30px;
}


.bottombutton {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  padding: 0 50px;
  margin-top: 50px;
}
</style>