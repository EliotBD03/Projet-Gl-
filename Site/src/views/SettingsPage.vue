<template>
  <!--Note pour Maxime, pour le change password, fais un mini formulaire qui prend l'adresse mail.
  Vérifie que l'utilisateur a bien rempli le champ.
  Stocke l'adresse mail dans les cookies puis redirige vers forgotten password
  ça évitera les répétitions :) -->
  <div class="main">
    <div class="header">
      <MainHeader text="Settings"/>
    </div>
    <div class="forms">
      <div class="form">
        <DropdownMain text="Choose your language..."/>
        <DropdownMain text="Add a language..."/>
        <div class="langbutton" @click.prevent.left="langChanged()">
        <GoButton text="Change language" :colore="'#B1B9FC'"/>
          </div>
      </div>
      <div class="form">
        <InputMain text="Enter your mail" type="mail" v-model="mail"/>
        <div class="changebutton" @click.prevent.left="changed()">
          <GoButton text="Change password" :colore="'#B1B9FC'"/>
        </div>
      </div>
    </div>
    <div class="bottombutton" @click.prevent.left="redirecting()">
      <!--Il va falloir vérif le rôle pour revenir il y a une méthode dans globalMethods-->
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
    }
  },
  methods: {
    changed() {
      if (!this.mail) Swal.fire("Please enter your mail");
      else {
        this.$cookies.set('mail', this.mail);
        this.$router.push({ name: 'ForgottenPassword' });
      }
    },
    langChanged() {
      Swal.fire("Your language has been changed !");
    },
    redirecting() {
      GlobalMethods.isAClient(this.$cookies.get('role'));
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