<template>
  <div class="main">
    <div class="header">
      <MainHeader text="header.settings"/>
    </div>
    <div class="forms">
      <div class="form">
        <DropdownMain :text="$t('settings.chooselanguage')" v-model="$i18n.locale"/>
        <div class="langbutton" @click.prevent.left="langChanged()">
        <GoButton text="button.changelanguage" :colore="'#B1B9FC'"/>
          </div>
      </div>
      <div class="form">
        <InputMain :text="$t('settings.entermail')" type="mail" v-model="mail"/>
        <div class="changebutton" @click.prevent.left="goForgot()">
          <GoButton text="button.changepwd" :colore="'#B1B9FC'"/>
        </div>
      </div>
    </div>
    <div class="bottombutton" @click.prevent.left="redirecting()">
      <GoButton text="header.home" :colore="'#B1B9FC'"/>
    </div>
    <div class="rightcancelbutton" @click.prevent.left="askDelete()">
      <GoButton text="Delete account" :colore="'#FF2D00'"/>
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
  /*Méthode pour charger la langue sauvegardée en cookie*/
  mounted() {
    if (this.$cookies.get("lang")) {
      this.$i18n.locale = this.$cookies.get("lang");
    } else {
      this.$cookies.set("lang", this.$i18n.locale)
    }
  },
  methods: {
    /*Sauvegarder la langue dans les cookies et afficher un message de confirmation*/
    langChanged() {
      this.$cookies.set("lang", this.$i18n.locale);
      Swal.fire(this.$t("alerts.languagechanged"));
    },
    /*Méthode pour rediriger vers la page d'accueil*/
    redirecting() {
      GlobalMethods.isAClient();
    },
    /*Méthode pour rediriger vers la page de changement de mot de passe*/
    goForgot(){
      if (!this.mail) {
        Swal.fire(this.$t("alerts.entermail"));
        return;
      }
      else {
        this.$cookies.set("mail", this.mail);
        GlobalMethods.disconnect("/forgottenpassword");
      }
    },
    async deleteAccount()
    {
      const requestOptions=
      {
        method:"POST",
        headers: {'Authorization': this.$cookies.get("token")}
      };
      try
      {
        const response = await fetch(`https://babawallet.alwaysdata.net/api/delete_account`, requestOptions);
        if(!response.ok)
        {
          if(response.status == 401)
            throw new Error("Token")
          else
          {
            const data = await response.json();
            throw new Error(data.error);
          }

        }
        else
        {
          Swal.fire(
            {
              icon: "success",
              title: "Success",
              text: "Your account has been deleted"
            }
          )
          this.$router.push({name: "login"});
        }
      }
      catch(error)
      {
        if(error.message === "Token")
        {
          this.$cookies.remove("token");
          this.$cookies.remove("role");
          Swal.fire('Your connection has expired');
          this.$router.push("/")
        }
        else
          Swal.fire("There are still some contract");  
        
      }
    },
    askDelete(){
      Swal.fire({
                  icon: "warning",
                  title: 'WARNING',
                  text: "are you sure you want to leave us :'(",
                  buttons:{
                    myButtonOk:{
                      text: 'Yes',
                      action: function()
                      {
                        this.deleteAccount();
                        Swal.close();
                      }
                    },
                    cancel: true
                  }
                  
                });
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
  z-index: 9999; 
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