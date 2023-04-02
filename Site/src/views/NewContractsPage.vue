
<template>
  <div class="main">
    <div class="header">
      <MainHeader text="header.newcontracts"/>
    </div>
    <div class="container">
      <div class="list">
        <div class="proposals">
          <p>PROPOSALS</p>
          <div v-for="proposal in proposals" :key="proposal.id">
            <div v-for="location in allLocations" :key="location.id">
              <div v-if="proposal.location==location.value">
              <span>{{ location.name }}</span>
              </div>
            </div>
            <p>{{ proposal.nameProvider }}</p>
            <p>{{ proposal.proposalName }}</p>
            <div @click.prevent.left="SeeMore(proposal)">
              <GoButton text="button.go" :colore="'#34c98e'"/>
            </div>
          </div>

        </div>
        <div v-if="notLastPage()" @click.prevent.left="loader()">
          <GoButton text="See more proposals" :colore="'#B1B9FC'"/>
        </div>
      </div>
      <div class="homeButton" @click.prevent.left="$router.push('/Home')">
      <GoButton text="header.home" :colore="'#B1B9FC'"/>
      </div>
    </div>

    <div class="checkboxleftside">
          <p>LOCATION</p>
          <span v-for="location in allLocations" :key="location.id">
            <input type="checkbox" :value="location.value" v-model="selectedLocations" v-on:click="checkOnlyOneLocation()">
            <span class="checkbox-label">{{ location.name }}</span> <br>
          </span>
          <p>ENERGY</p>
          <span v-for="energy in allEnergies" :key="energy.id">
            <input type="checkbox" :value="energy.name" v-model="selectedEnergies" v-on:click="checkOnlyOneEnergy()">
            <span class="checkbox-label">{{ energy.name }}</span>
          </span>
    </div>

    <div class="applyFilterButton" @click.prevent.left="applyFilter()">
      <GoButton text="Apply filter(s)" :colore="'#2962FF'"/>
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
      selectedLocations : ["null"], //used to filter the location
      selectedEnergies : ["null"], //used to filter the energies
      allLocations: 
      [
        {name: "Brussels", value: "100"},
        {name: "Flanders", value: "010"},
        {name: "Wallonia", value: "001"}
      ],
      allEnergies:
      [
        {name: "electricity"},
        {name: "water"},
        {name: "gas"}
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
        console.log(`${this.linkApi}page?page=${this.nbr}&energy_category=${this.selectedEnergies}&region_category=${this.selectedLocations}&limit=2`)
        const response = await fetch(`${this.linkApi}page?page=${this.nbr}&energy_category=${this.selectedEnergies}&region_category=${this.selectedLocations}&limit=2`, requestOptions);
        if(!response.ok)
        {
          const data = await response.text();
          if(response.status == 401 && data.trim() === '')
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
      this.$router.push({name: "ContractInformation"});
    },
    applyFilter()
    {
      if(this.selectedEnergies.length == 0) this.selectedEnergies = ["null"]
      if(this.selectedLocations.length == 0) this.selectedLocations = ["null"]
      
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
.checkboxleftside{
  display: relative;
  left: 0%;
  flex-direction: column;
  padding: 0 50px;
  margin-left: 10 10px;
}
</style>