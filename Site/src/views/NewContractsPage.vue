<template>
    <div class="main">
        <div class="header">
            <MainHeader text="header.newcontracts"/>
        </div>
        <div class="proposals">
            <div class="cards" v-for="proposal in proposals" :key="proposal.id">
                <div class="text">
                    <p><b>{{ $t("proposal.location") }} :</b></p>
                    {{ convertLocation(proposal.location) }}
                </div>
                <p><b>{{ $t("account.provider") }} :</b></p>
                {{ proposal.nameProvider }}
                <p><b>{{  $t("proposal.proposalname") }}: </b></p>
                {{ proposal.proposalName }}
                <p><b>{{ $t("proposal.typeofenergy") }}: </b></p>
                {{ proposal.typeOfEnergy.charAt(0).toUpperCase() + proposal.typeOfEnergy.slice(1) }}
            <div @click.prevent.left="seeMore(proposal)">
                <GoButton text="button.go" :colore="'#34c98e'"/>
            </div>
            </div>
        </div>
    <div class="checkboxleftside">
        <div class="filters">
            <div class="location">
                <p><b>{{ $t("proposal.location") }} :</b></p>
                <span v-for="location in allLocations" :key="location.id">
              <input type="checkbox" :value="location.value" v-model="selectedLocations" v-on:click="checkOnlyOneLocation()">
              <span class="checkbox-label">{{ location.name }}</span> <br>
            </span>
            </div>
            <div class="energy">
                <p><b>{{ $t("proposal.energy") }} :</b></p>
                <span v-for="energy in allEnergies" :key="energy.id">
              <input type="checkbox" :value="energy.value" v-model="selectedEnergies" v-on:click="checkOnlyOneEnergy()">
              <span class="checkbox-label">{{ energy.name }}</span> <br>
            </span>
            </div>
        </div>
        <div class="applyFilterButton" @click.prevent.left="applyFilter()">
            <GoButton text="button.applyfilters" :colore="'#2962FF'"/>
        </div>
    </div>
    <div class="bottombuttons">
        <div class="homeButton" @click.prevent.left="$router.push('/Home')">
            <GoButton text="header.home" :colore="'#B1B9FC'"/>
        </div>
        <div v-if="notLastPage()" @click.prevent.left="loader()">
            <GoButton text="button.seemore" :colore="'#B1B9FC'"/>
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
    data(){
        return{
            linkApi: "https://babawallet.alwaysdata.net/api/client/proposals/",
            nbr: 1,
            proposals:[],
            loading: false,
            lastPage: 0,
            selectedLocations : "", //used to filter the location
            selectedEnergies : "", //used to filter the energies
            allLocations:
                [
                    {name: this.$t("proposal.wallonia"), value: "100"},
                    {name: this.$t("proposal.flanders"), value: "010"},
                    {name: this.$t("proposal.brussels"), value: "001"}
                ],
            allEnergies:
                [
                    {name: this.$t("proposal.electricity"), value: "electricity"},
                    {name: this.$t("proposal.water"), value: "water"},
                    {name: this.$t("proposal.gas"), value: "gas"}
                ]
        }
    },
    created(){
        this.getPage();
        GlobalMethods.getCurrentLanguage();
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
                    var query = `${this.linkApi}page?page=${this.nbr}&region_category=${this.selectedLocations}&energy_category=${this.selectedEnergies}&limit=2`;
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
                            Swal.fire(this.$t("proposal.noproposal"));
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
                        Swal.fire(this.$t("alerts.connectionexpired"));
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
                sessionStorage.setItem('providerId', proposal.providerId)
                sessionStorage.setItem('proposalName', proposal.proposalName)
                this.$router.push('/contractInformation');
            },
            applyFilter()
            {
                this.nbr = 1;
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
                const locToString = [this.$t("proposal.brussels"), this.$t("proposal.flanders"), this.$t("proposal.wallonia")];
                const result = [];
                
                for(let i = 0; i < location.length; i++)
                    if(location[i] == "1")
                        result.push(locToString[i]);

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
    height: 120vh;
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
