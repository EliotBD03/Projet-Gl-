<template>
    <div class="main">
        <div class="header">
            <MainHeader text="header.newcontracts"/>
        </div>
        <div class="proposals">
            <div class="cards" v-for="proposal in proposals" :key="proposal.id">
                <div class="text">
                    <p><b>Location :</b></p>
                    {{ convertLocation(proposal.location) }}
                </div>
                <p><b>Provider name:</b></p>
                {{ proposal.nameProvider }}
                <p><b>Offer name: </b></p>
                {{ proposal.proposalName }}
                <p><b>Energy type: </b></p>
                {{ proposal.typeOfEnergy.charAt(0).toUpperCase() + proposal.typeOfEnergy.slice(1) }}
            <div @click.prevent.left="seeMore(proposal)">
                <GoButton text="button.go" :colore="'#34c98e'"/>
            </div>
            </div>
        </div>
    <div class="checkboxleftside">
        <div class="filters">
            <div class="location">
                <p><b>LOCATION</b></p>
                <span v-for="location in allLocations" :key="location.id">
              <input type="checkbox" :value="location.value" v-model="selectedLocations" v-on:click="checkOnlyOneLocation()">
              <span class="checkbox-label">{{ location.name }}</span> <br>
            </span>
            </div>
            <div class="energy">
                <p><b>ENERGY</b></p>
                <span v-for="energy in allEnergies" :key="energy.id">
              <input type="checkbox" :value="energy.name" v-model="selectedEnergies" v-on:click="checkOnlyOneEnergy()">
              <span class="checkbox-label">{{ energy.name }}</span> <br>
            </span>
            </div>
        </div>
        <div class="applyFilterButton" @click.prevent.left="applyFilter()">
            <GoButton text="Apply filter(s)" :colore="'#2962FF'"/>
        </div>
    </div>
    <div class="bottombuttons">
        <div class="homeButton" @click.prevent.left="$router.push('/Home')">
            <GoButton text="header.home" :colore="'#B1B9FC'"/>
        </div>
        <div v-if="notLastPage()" @click.prevent.left="loader()">
            <GoButton text="See more proposals" :colore="'#B1B9FC'"/>
        </div>
    </div>
    </div>
</template>

<script>
import MainHeader from "@/components/MainHeader.vue";
import GoButton from "@/components/GoButton.vue";
import  Swal  from "sweetalert2";
import GlobalMethods from "@/components/GlobalMethods.vue";
export default {
    components: {
        MainHeader,
        GoButton,
    },
    /*Méthode pour charger la langue sauvegardée en cookie*/
    mounted()
    {
        if (this.$cookies.get("lang"))
        {
            this.$i18n.locale = this.$cookies.get("lang");
        }
        else
        {
            this.$cookies.set("lang", this.$i18n.locale)
        }
    },
    data(){
        return{
            linkApi: "https://babawallet.alwaysdata.net/api/client/proposals/",
            nbr: 1,
            proposals:[],
            loading: false,
            lastPage: 0,
            selectedLocations : [], //used to filter the location
            selectedEnergies : [], //used to filter the energies
            allLocations:
                [
                    {name: "Brussels", value: "100"},
                    {name: "Flanders", value: "010"},
                    {name: "Wallonia", value: "001"}
                ],
            allEnergies:
                [
                    {name: "Electricity"},
                    {name: "Water"},
                    {name: "Gas"}
                ]
        }
    },
    created(){
        this.getPage();
    },
    methods:
        {
            async getPage()
            {
                const requestOptions =
                    {
                        method: "GET",
                        headers: {'Authorization' : this.$cookies.get("token")},
                    };
                this.loading = true;
                try
                {
                    var query = `${this.linkApi}page?page=${this.nbr}&energy_category=${this.selectedEnergies}&region_category=${this.selectedLocations}&limit=2`;
                    console.log(this.selectedEnergies, this.selectedLocations);
                    if(this.selectedEnergies.length == 0 && this.selectedLocations.length == 0)
                        query = `${this.linkApi}page?page=${this.nbr}&limit=2`;
                    else if(this.selectedEnergies.length == 0)
                        query = `${this.linkApi}page?page=${this.nbr}&region_category=${this.selectedLocations}&limit=2`;
                    else if(this.selectedLocations.length == 0)
                        query = `${this.linkApi}page?page=${this.nbr}&energy_category=${this.selectedEnergies}&limit=2`;

                    const response = await fetch(query, requestOptions);
                    if(!response.ok)
                    {
                        if(response.status == 401)
                        {
                            throw new Error("Token");
                        }
                        else
                        {
                            const data = await response.json();
                            throw new Error(data.error);
                        }
                    }
                    else
                    {
                        const data = await response.json();
                        this.lastPage = data.last_page;
                        if(this.lastPage == 0)
                        {
                            this.loading = true;
                            Swal.fire("No proposal");
                        }
                        else if(this.lastPage >= this.nbr)
                        {
                            this.proposals.push(data.proposals);
                            this.proposals = this.proposals.flat();
                            this.loading = false;
                        }
                    }
                }
                catch(error)
                {
                    if(error.message === "Token")
                    {
                        this.$cookies.remove("token");
                        this.$cookies.remove("role");
                        Swal.fire('Your connection has expired');
                        this.$router.push("/");
                    }
                    else
                    {
                        GlobalMethods.errorApi(error.message);
                    }
                }
            },
            loader()
            {
                if(!this.loading)
                {
                    this.nbr++;
                    this.proposal = [];
                    this.getPage();
                }
            },
            notLastPage()
            {
                if(this.lastPage == this.nbr || this.loading == true)
                    return false;
                return true;
            },
            seeMore(proposal)
            {
                console.log(proposal)
                sessionStorage.setItem('providerId', proposal.providerId)
                sessionStorage.setItem('proposalName', proposal.proposalName)
                this.$router.push('/contractInformation');
            },
            applyFilter()
            {
                this.proposals = []
                this.getPage();
            },
            checkOnlyOneEnergy()
            {
                this.selectedEnergies = [];
            },
            checkOnlyOneLocation()
            {
                this.selectedLocations = [];
            },
            convertLocation: function(location) {
                const result = [];

                if (location >= 100) {
                    result.push('Wallonie');
                    location -= 100;
                }

                if (location >= 10) {
                    result.push('Flandre');
                    location -= 10;
                }

                if (location >= 1) {
                    result.push('Bruxelles-Capitale');
                }

                return result.join(' - ');
            },
        }
};
</script>

<style scoped>
.main {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-evenly;
    height: 100vh;
}

.proposals {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
}

.header {
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
}

.checkboxleftside {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}

.bottombuttons {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-evenly;
}

.location {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin: 20px;
}

.energy {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin: 20px;
}

.filters {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
    margin: 20px;
}

.cards {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-evenly;
    width: 250px;
    height: 400px;
    box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
    margin: 10px;
    border-radius: 30px;
}

.text {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin: 10px;
}
</style>
