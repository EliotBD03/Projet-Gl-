<template>
  <div class="main">
    <div class="header">
      <MainHeader text="header.settings"/>
    </div>
    <div class="forms">
      <div class="form">
        <DropdownMain text="Choose your language..." v-model="$i18n.locale"/>
        <DropdownMain text="Add a language..."/>
        <div class="langbutton" @click.prevent.left="langChanged()">
        <GoButton text="Change language" :colore="'#B1B9FC'"/>
          </div>
      </div>
      <div class="form">
        <InputMain text="Enter your mail" type="mail" v-model="mail"/>
        <div class="changebutton" @click.prevent.left="goForgot()">
          <GoButton text="Change password" :colore="'#B1B9FC'"/>
        </div>
      </div>
    </div>
    <div class="bottombutton" @click.prevent.left="redirecting()">
      <GoButton text="Home" :colore="'#B1B9FC'"/>
    </div>
  </div>
</template>

<script>
import MainHeader from "@/components/MainHeader.vue";
import GoButton from "@/components/GoButton.vue";
import DropdownMain from "@/components/DropdownMain.vue";
import Swal from 'sweetalert2';
import InputMain from "@/components/InputMain.vue";
import GlobalMethods from "@/components/GlobalMethods.vue";
export default {
  components: {
    InputMain,
    DropdownMain,
    MainHeader,
    GoButton,
  },
  data() {
    return {
      mail: "",
      test: "",
    }
  },
  methods: {
    langChanged() {
      Swal.fire("Your language has been changed !");
    },
    redirecting() {
      GlobalMethods.isAClient(this.$cookies.get('role'));
    },
    goForgot(){
      GlobalMethods.disconnect("/forgottenpassword")
    },
    setLocale(locale) {
      this.$i18n.locale = locale;
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

.form {
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