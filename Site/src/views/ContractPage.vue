<template>
    <div class="main">
        <div class="header">
            <MainHeader text="header.contracts"/>
        </div>
        <div class="allcards">
            <div class=cards v-for="contract in listContracts" :key="contract.id">
                <div class="texte">
                    <p class="name"> Provider name : </p>
                    <p>{{ contract.providerName }}</p>
                    <p><b>EAN Code :</b></p>
                    <p>{{ contract.ean }}</p>
                </div>
            </div>
            <div v-if="notLastPage()" @click.prevent.left="loader()">
                <GoButton text="See more contracts" :colore="'#B1B9FC'"/>
            </div>
        </div>
        <div class="homebutton" @click.prevent.left="redirecting()">
            <GoButton text="header.home" :colore="'#B1B9FC'"/>
        </div>
    </div>
</template>
<script>
import MainHeader from "@/components/MainHeader.vue";
import GoButton from "@/components/GoButton.vue";
import Swal from 'sweetalert2';
import GlobalMethods from "@/components/GlobalMethods.vue";
export default {
    components : {
        GoButton,
        MainHeader,
    },
    /*Méthode pour charger la langue sauvegardée en cookie*/
    mounted() {
        if (this.$cookies.get("lang")) {
            this.$i18n.locale = this.$cookies.get("lang");
        } else {
            this.$cookies.set("lang", this.$i18n.locale)
        }
    },
    data(){
        return{
            linkApi : "https://babawallet.alwaysdata.net/api/client/contracts/",
            nbr : 1,
            loading : false,
            lastPage : 0,
            listContracts: []
        }},
    /*Au moment de la création on récupère déjà la première page de l'api*/
    created() {
        this.getPage();
    },
    methods: {
        /*Méthode permettant de récupérer les pages des wallets de l'Api avec le bouton seeMore */
        async getPage(){
            const requestOptions = {
                method: "GET",
                headers: {'Authorization' : this.$cookies.get("token")},
            };
            this.loading = true; //bloquer les demandes de loader pendant ce temps.
            try {
                const response = await fetch(`${this.linkApi}page?page=${this.nbr}&limit=3`, requestOptions);
                if (!response.ok) {
                    if(response.status == 401){
                        throw new Error("Token");
                    }
                    else{
                        const data = await response.json();
                        throw new Error(data.error);
                    }
                } else {
                    const data = await response.json();
                    this.lastPage = data.last_page;
                    if(this.lastPage == 0){
                        this.loading = true;
                        Swal.fire('No contracts');
                    }
                    else if(this.lastPage >= this.nbr){
                        this.listContracts.push(data.contracts); //ajouter la suite de la réponse à la liste
                        this.listContracts = this.listContracts.flat(); //transforme une liste multidimensionnelle en une liste à une seule dimension
                        this.loading = false;
                    }
                }
            } catch(error) {
                if(error.message === "Token") {
                    GlobalMethods.errorToken();
                }
                else {
                    if(this.nbr === 1){
                        this.loading = true;
                        Swal.fire('No contracts');
                    }
                    else{
                        GlobalMethods.errorApi(error.message);
                    }
                }
            }
        },
        /*Lorsque l'utilisessionStorage.removeItem('name_proposal')
                        )sateur appuie sur SeeMore, cette méthode est appelée
        pour augmenter le nombre de la page et appeler getPage*/
        loader()
        {
            if(!this.loading)
            {
                this.nbr++;
                this.getPage();
            }
        },
        /*Méthode permettant de vérifier si la dernière page n'a pas encore été chargée
        et si on est pas en cours de chargement*/
        notLastPage(){
            if(this.lastPage == this.nbr || this.loading == true){
                return false;
            }
            return true;
        },
        redirecting(){
            GlobalMethods.isAClient();
        }
    }
}
</script>

<style scoped>

.header {
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
}

.main {
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    align-items: center;
    height: 100vh;
}

.homebutton {
    display: flex;
    justify-content: center;
}

.allcards {
    display: flex;
    align-items: center;
    flex-direction: row;
    justify-content: space-evenly;
}

.cards {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-evenly;
    width: 250px;
    height: 450px;
    box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
    margin: 20px;
    border-radius: 30px;
}

.texte {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin: 50px;
}

.name {
    color: rgb(138, 150, 253);
    font-size: 30px;
}
</style>