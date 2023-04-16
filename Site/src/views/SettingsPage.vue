<template>
    <div class="main">
        <div class="header">
            <MainHeader text="header.settings"/>
        </div>
        <div class = "id"> 
            <p>Id = {{ userId }}</p> 
        </div>
        <div class="forms">
            <div class="form">
                <DropdownMain :text="$t('settings.chooselanguage')" v-model="language"/>
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
        <div class="bottombutton">
        <div class="bottombutton" @click.prevent.left="redirecting()">
            <GoButton text="header.home" :colore="'#B1B9FC'"/>
        </div>
        <div class="rightcancelbutton" @click.prevent.left="askDelete()">
            <GoButton text="button.deleteaccount" :colore="'#FF2D00'"/>
        </div>
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
import jwt_decode from 'jwt-decode';
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
            language: this.$i18n.locale,
            userId : ""
        }
    },
    watch: {
        language() {
            this.$i18n.locale = this.language;
        }
    },
    created() {
        GlobalMethods.getCurrentLanguage();
        this.getUserId();
    },
    methods: {
        /*Sauvegarder la langue dans les cookies et afficher un message de confirmation*/
        langChanged() {
            const requestsOptions = {
                method: "PUT",
                headers: {'Authorization': this.$cookies.get("token")},
            }
            fetch("https://babawallet.alwaysdata.net/api/common/languages/actual_language/" + this.language, requestsOptions)
                .then(response => {
                    if(!response.ok){
                        return response.json().then(json => Promise.reject(json));
                    }
                    return response.json();
                })
                .then(Swal.fire({
                    icon: 'success',
                    title: this.$t('alerts.good'),
                    text: this.$t('alerts.languagechanged'),
                }))
                .then(GlobalMethods.isAClient())
                .catch(error => {
                    GlobalMethods.errorApi(error.error);
                });
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
        deleteAccount()
        {
            const requestOptions=
                {
                    method:"GET",
                    headers: {'Authorization': this.$cookies.get("token")},
                };
            fetch(`https://babawallet.alwaysdata.net/log/delete_user`, requestOptions)
                .then(response => {
                    if(!response.ok)
                    {
                        if(response.status == 404)
                            this.$router.push({name: "NotFound"});
                        else
                            return response.json().then(json => Promise.reject(json));
                    }
                    else
                    {
                        Swal.fire(
                            {
                                icon: "success",
                                title: this.$t("alerts.good"),
                                text: this.$t("alerts.deletedaccount")
                            }
                        )
                        this.$cookies.remove("token");
                        this.$cookies.remove("role");
                        this.$router.push({name: "login"});
                    }     
                })
                .catch(error => {
                    if(error.error === "error.unauthorizedAccess")
                        GlobalMethods.errorToken();
                    else if(error.message === "stillContract")
                        Swal.fire(this.$t("alerts.stillcontracts"));
                    else
                        GlobalMethods.errorApi(error.error);
                });
        },
        askDelete(){
        Swal.fire({
            imageHeight: 200,
            imageWidth: 250,
            imageUrl: "https://media.giphy.com/media/Q6WPVzFU8LcBWWgQE1/giphy-downsized-large.gif",
            animation: true,
            imageAlt: "Puss in boots",
            icon: "warning",
            title: this.$t("alerts.warning"),
            text: this.$t("alerts.areyousure"),
            confirmButtonText: this.$t("alerts.yes"),
            }).then((result) => {
                if(result.isConfirmed)
                {
                    this.deleteAccount();
                    Swal.close();
                }
            });
        },
        /**
         * Méthode permettant d'obtenir l'id du client.
         * 
         * @author Extension Claire
         */
        getUserId(){
          const token = this.$cookies.get('token');
          const decode = jwt_decode(token);
          this.userId = decode.id;
        }
    }
};
</script>

<style scoped>

.main {
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    height: 105vh;
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

.id{
  position: fixed;
  margin-top: 20px;
  margin-right: 20px;
  top: 0;
  right: 0;
  z-index: 9999;
  font-size: 25px;
}
</style>
