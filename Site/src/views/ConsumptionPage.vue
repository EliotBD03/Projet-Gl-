<template>
  <!--Idée de la page de consommations des portefeuilles-->
  <div class="main">
    <div class="header">
      <MainHeader text="header.consumption"/>
    </div>
    <div class="topbutton">
      <div @click.prevent.left="showTable()">
        <GoButton text="button.table" :colore="'#34c98e'"/>
      </div>
      <div @click.prevent.left="exportData()">
        <GoButton text="button.export" :colore="'#34c98e'"/>
      </div>
      <div @click.prevent.left="showGraphic()">
        <GoButton text="button.graphic" :colore="'#34c98e'"/>
      </div>
    </div>
    <div class="infos">
      <div class="container">
        <canvas ref="myChart"></canvas>
      </div>
      <div class="newconsumption" >
        <InputMain type="date" id="dateNewConsumption" value="2020-01-01" min="2020-01-01" max="2099-12-31"/>
        <InputMain type="number" id="dataNewConsumption" min="0"/>
        <div @click.prevent.left="post()">
          <GoButton type="submit" text="button.add" :colore="'#34c98e'"/>
        </div>
      </div>
    </div>
    <div class="bottombutton" @click.prevent.left="back()">
      <GoButton text="button.back" :colore="'darkblue'"/>
    </div>
  </div>
</template>

<script>
import GoButton from "@/components/GoButton.vue";
import MainHeader from "@/components/MainHeader.vue";
import Chart from "chart.js/auto";
import InputMain from "@/components/InputMain.vue";
import Swal from "sweetalert2";
import GlobalMethods from "@/components/GlobalMethods.vue";
import Papa from "papaparse";

export default {
  components: {
    GoButton,
    MainHeader,
    InputMain
  },

  data(){
    return{
      mode : true,
      ean : sessionStorage.getItem('ean'),
      listValue : [1, 2, 3, 4],
      listDate : ["11/01/2202","02/08/2002","11/01/2701","12/04/2502",],
      forcing : false
    }},
  
  /*Méthode pour charger la langue sauvegardée en cookie*/
  mounted() {
    if (this.$cookies.get("lang")) {
      this.$i18n.locale = this.$cookies.get("lang");
    } else {
      this.$cookies.set("lang", this.$i18n.locale)
    }

    this.showTable();
  },

  methods: {
    exportData() {
      const table = [];
      table.push(["date", "data"]);
      for (let i = 0; i < this.listValue.length; i++) {
        table.push([this.listDate[i], this.listValue[i]]);
      }

      // exportation en CSV
      const csv = Papa.unparse(table);
      const lien = document.createElement("a");
      lien.setAttribute("href", "data:text/csv;charset=utf-8," + encodeURIComponent(csv));
      lien.setAttribute("download", "table.csv");
      document.body.appendChild(lien);
      lien.click();
      document.body.removeChild(lien);
      },

    showTable() {
      if(this.mode) {
        this.mode = !this.mode;
      }
    },

    showGraphic() {
      if(!this.mode) {
        const ctx = this.$refs.myChart.getContext('2d');
        const data = {
          labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
          datasets: [{
          label: 'My First Dataset',
          data: [65, 59, 80, 81, 56, 55, 40],
          fill: false,
          borderColor: 'rgb(75, 192, 192)',
          tension: 0.1
          }, {
          label: 'My Second Dataset',
          data: [28, 48, 40, 19, 86, 27],
          fill: false,
          borderColor: 'rgb(192, 75, 192)',
          tension: 0.1
          }]
        };
        const options = {};
        new Chart(ctx, {
          type: 'line',
          data: data,
          options: options
        });

        this.mode = !this.mode;
      }
    },

    checkArgs() {
      const date = document.getElementById("dateNewConsumption").value;
      const data = document.getElementById("dataNewConsumption").value;
      if(data == '')
      {
        Swal.fire(this.$t("Entrez une valeur !"));//alerts.data
        return false;
      }
      else if(data < 0)
      {
        Swal.fire(this.$t("Entrez une consommation positive !"));//alerts.dataSigne
        return false;
      }

      this.listValue.push(data);
      this.listDate.push(date);

      return true;
    },
    
    post (){
      if(this.checkArgs())
      {
        const requestOptions = {
          method: "POST",
          headers: {'Authorization': this.$cookies.get("token")},
          body: JSON.stringify({ ean: this.ean, list_value: this.listValue, list_date: this.listDate, forcing: this.forcing})
        };
        fetch("https://babawallet.alwaysdata.net/api/common/consumptions", requestOptions)
            .then(response => {
              if(!response.ok){
                const data = response.text();
                if(response.status == 401 && data.trim() === ''){
                    throw new Error("Token");
                }
                else{
                  return response.json().then(json => Promise.reject(json)); 
                }
              }
              else{
                Swal.fire({
                  icon: 'success',
                  title: 'Good !',
                  text: 'Consumption add !'
                })
                this.listValue = [];
                this.listDate = [];
              }
            })
            .catch(error => {
              if (error.message === "Token") {
              this.$cookies.remove("token");
              this.$cookies.remove("role");
              Swal.fire('Your connection has expired');
              this.$router.push("/");
              } 
              else {
                GlobalMethods.errorApi(error.error);
              }
            });
          }
        },
    back() {
      sessionStorage.clear();
      this.$router.push('/wallets');
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
  height: 20vh;
  z-index: 9999;
}
.topbutton {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  margin-top: 100px;
}

.infos {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-evenly;
  padding: 0 50px;
  margin-top: 25px;
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

.bottombutton {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 50px;
  margin-top: 25px;
}
</style>
