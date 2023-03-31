<template>  <div class="main">
    <div class="header">
      <MainHeader text="header.consumption"/>
    </div>
    <div class="topbutton">
      <div @click.prevent.left="showTable()">
        <GoButton text="button.table" :colore="'#34c98e'"/>
      </div>
      <div @click.prevent.left="exportData()">
        <GoButton text="Export" :colore="'#34c98e'"/><!--trad-->
      </div>
      <div>
        <input type="file" id="csv-file" accept=".csv"/>
        <div @click.prevent.left="importData()">
          <GoButton text="Import" :colore="'#34c98e'"/><!--trad-->
        </div>
        </div>
      <div @click.prevent.left="showGraphic()">
        <GoButton text="button.graphic" :colore="'#34c98e'"/>
      </div>
    </div>
    <div class="middlebutton">
      <button class="arrow-button" @click.prevent.left="getDataBefore()">
        <span class="arrow">&larr;</span>
      </button>
      <div class="infos">
        <div class="container">
          <canvas ref="myChart"></canvas>
          <div id="table">
            <div class="table">
              <div class="cellule">Date</div>
              <div class="cellule">Data</div>
            </div>
            <div class="table">
              <div class="row" v-for="date in listDate" :key="date.id">
                <div class="cellule">{{ date }}</div>
              </div>
              <div class="row" v-for="data in listValue" :key="data.id">
                <div class="cellule">{{ data }}</div>
              </div>
            </div>
          </div>
        </div>
        <div class="newconsumption" >
          <InputMain type="date" id="dateNewConsumption" value="2020-01-01" min="2020-01-01" max="2099-12-31"/>
          <InputMain type="number" id="dataNewConsumption" min="0"/>
          <div @click.prevent.left="post()">
            <GoButton type="submit" text="button.add" :colore="'#34c98e'"/>
          </div>
        </div>
      </div>
      <button class="arrow-button" @click.prevent.left="getDataAfter()">
        <span class="arrow">&rarr;</span>
      </button>
    </div>
    <div class="bottombutton">
      <div @click.prevent.left="back()">
        <GoButton text="button.back" :colore="'darkblue'"/>
      </div>
      <div id="modeTime" @click.prevent.left="changeModTime()">
        <GoButton :text=labelButtonDisplay :colore="'#34c98e'"/><!--trad-->
      </div>
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
      mode : false,
      ean : sessionStorage.getItem('ean'),
      date : "",
      isAfter : true,
      listValue : [4, 2, 6, 7, 1, 5, 5, 3, 5],
      listValue2 : [],
      listDate : ["2001-01-01", "2001-01-02", "2001-01-03", "2001-01-04", "2001-01-05", "2001-01-06", "2001-01-07", "2001-01-08", "2001-01-09", ],
      isComparaison : false,
      listNewValue : [],
      listNewDate : [],
      forcing : false,
      chart : null,
      isDisplayDay : true,
      month : "",
      year : "",
      labelButtonDisplay : "DisplayMonth"//trad
    }},
  
  /*Méthode pour charger la langue sauvegardée en cookie*/
  mounted() {
    if (this.$cookies.get("lang")) {
      this.$i18n.locale = this.$cookies.get("lang");
    } else {
      this.$cookies.set("lang", this.$i18n.locale)
    }

    this.getConsumption();
    this.showGraphic();
  },

  methods: {
    exportData() {
      const table = [];
      table.push(["date", "data"]);
      for (let i = 0; i < this.listValue.length; i++) {
        table.push([this.listDate[i], this.listValue[i]]);
      }

      const csv = Papa.unparse(table);
      const lien = document.createElement("a");
      lien.setAttribute("href", "data:text/csv;charset=utf-8," + encodeURIComponent(csv));
      lien.setAttribute("download", "table.csv");
      document.body.appendChild(lien);
      lien.click();
      document.body.removeChild(lien);
    },

    importData() {
      this.listNewDate = [];
      this.listNewValue = [];

      const dateRegex = /\d{4}-\d{2}-\d{2}/;

      const fileInput = document.getElementById('csv-file');
      Papa.parse(fileInput.files[0] , {
        complete: (results) => {
          const tmp = results.data.slice(1);
          for(let i = 0; i < tmp.length; i++) {
            if(dateRegex.test(tmp[i][0])) {
              this.listNewDate.push(tmp[i][0]);
              this.listNewValue.push(tmp[i][1]);
            }
          }
          this.post();
        }
      });
    },

    getDataBefore() {
      if(this.isDisplayDay) {
        this.isAfter = false;
        this.date = this.listDate[0];
        this.getConsumption();
      } else {
          this.month--;
          if(this.month <= 0) {
            this.month = 12;
            this.year--;
        }
      this.getConsumptionOfMonth();
      }
    },

    getDataAfter() {
      if(this.isDisplayDay) {
        this.isAfter = true;
        this.date = this.listDate[-1];
        this.getConsumption();
      } else {
          this.month++;
          if(this.month >= 13) {
            this.month = 1;
            this.year++;
        }
      this.getConsumptionOfMonth();
      }
    },

    showTable() {
      if(this.mode) {
        this.mode = !this.mode;
        this.chart.destroy();
        document.getElementById("table").style.display = "flex";
      }
    },

    showGraphic() {
      if(!this.mode) {
        this.mode = !this.mode;
        document.getElementById("table").style.display = "none";

        const ctx = this.$refs.myChart.getContext('2d');
        const data = {
          labels: this.listDate,
          datasets: [{
          label: 'Your Consumption',//trad
          data: this.listValue,
          fill: false,
          borderColor: 'rgb(75, 192, 192)',
          tension: 0.1
          }]
        };

        if(this.isComparaison) {
          data.datasets[0] += {
            label: 'Other Consumption',//trad
            data: this.listValue2,
            fill: false,
            borderColor: 'rgb(192, 75, 192)',
            tension: 0.1
            }
          }

        this.chart = new Chart(ctx, {
          type: 'line',
          data: data,
        });

        this.chart.update();
      }
    },

    checkArgs() {
      if(this.listNewDate.length > 0 && this.listNewValue.length == this.listNewDate.length) {
        return true;
      }

      const date = document.getElementById("dateNewConsumption").value;
      const data = document.getElementById("dataNewConsumption").value;
      if(data == '')
      {
        Swal.fire(this.$t("Entrez une valeur !"));//trad
        return false;
      }
      else if(data < 0)
      {
        Swal.fire(this.$t("Entrez une consommation positive !"));//trad
        return false;
      }

      this.listNewDate.push(date);
      this.listNewValue.push(data);

      return true;
    },
    
    async getConsumption() {
      const requestOptions = {
        method: "GET",
        headers: {'Authorization' : this.$cookies.get("token")},
      };
      try {
        const response = await fetch("https://babawallet.alwaysdata.net/api/common/consumptions/" + this.ean + "?date="+ this.date + "&is_after=" + this.isAfter, requestOptions);
        if (!response.ok) { 
          const data = await response.text();
          if(response.status == 401 && data.trim() === ''){
            throw new Error("Token");
          }
          else{
            const data = await response.json();
            throw new Error(data.error);
          }
        } else {
          const data = await response.json(); 

          if(this.isAfter) {
            this.listDate.push(data.listConsumption.keys);
            this.listValue.push(data.listConsumption.values);

            if(this.listDate.length > 50) {
              this.listDate = this.listDate.slice(10);
              this.listValue = this.listValue.slice(10);
            }
          } else {
            this.listDate = data.listConsumption.keys + this.listDate;
            this.listValue = data.listConsumption.values + this.listValue;
            
            if(this.listDate.length > 50) {
              this.listDate = this.listDate.slice(0, 40);
              this.listValue = this.listValue.slice(0, 40);
            }
          } 
        }
      } catch(error) {
          if(error.message === "Token") {
            this.$cookies.remove("token");
            this.$cookies.remove("role");
            Swal.fire('Your connection has expired');//trad
            this.$router.push("/");
          } 
          else {  
            GlobalMethods.errorApi(error.message);
          }
      }
    },

    async getConsumptionOfMonth() {
      const requestOptions = {
        method: "GET",
        headers: {'Authorization' : this.$cookies.get("token")},
      };
      try {
        const response = await fetch("https://babawallet.alwaysdata.net/api/common/consumptions_month/" + this.ean + "?year=" +  this.year +  "&month=" + this.month, requestOptions);
        if (!response.ok) { 
          const data = await response.text();
          if(response.status == 401 && data.trim() === ''){
            throw new Error("Token");
          }
          else{
            const data = await response.json();
            throw new Error(data.error);
          }
        } else {
          const data = await response.json(); 

          this.listDate = data.listConsumption.keys;
          this.listValue = data.listConsumption.values;

          if(this.listDate.length > 0) {
            this.year = this.listDate[0].slice(0, 4);
            this.month = this.listDate[0].slice(5, 7);
          }
        }
      } catch(error) {
          if(error.message === "Token") {
            this.$cookies.remove("token");
            this.$cookies.remove("role");
            Swal.fire('Your connection has expired');//trad
            this.$router.push("/");
          } 
          else {  
            GlobalMethods.errorApi(error.message);
          }
      }
    },

    post (){
      if(this.checkArgs())
      {
        const requestOptions = {
          method: "POST",
          headers: {'Authorization': this.$cookies.get("token")},
          body: JSON.stringify({ ean: this.ean, list_value: this.listNewValue, list_date: this.listNewDate, forcing: this.forcing})
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
                const data = response.json(); 
                if(!data.valueAlreadyDefine) {
                  Swal.fire({
                    icon: 'success',
                    title: 'Nickel !',//trad
                    text: 'La consommation a été ajouté'//trad
                  })
                  this.listNewValue = [];
                  this.listNewDate = [];

                  if(this.isDisplayDay) {
                    this.getConsumption();
                  } else {
                    this.getConsumptionOfMonth();
                  }
                } else {
                  Swal.fire({
                    title: "Êtes-vous sûr de vouloir changer une valeur ?",//trad
                    text: "Vous ne pourrez plus récupérer la valeur!",//trad
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#3085d6',
                    cancelButtonColor: '#d33',
                  }).then((result) => {
                    if (result.isConfirmed) {
                      this.forcing = true;
                      this.post();
                      this.forcing = false;
                    }
                  })
                }
              }
            })
            .catch(error => {
              if (error.message === "Token") {
              this.$cookies.remove("token");
              this.$cookies.remove("role");
              Swal.fire('Your connection has expired');//trad
              this.$router.push("/");
              } 
              else {
                GlobalMethods.errorApi(error.error);
              }
            });
          }
        },

    changeModTime() {
      this.isDisplayDay = !this.isDisplayDay;

      if(this.isDisplayDay) {
        this.labelButtonDisplay = "DisplayMonth";//trad
        this.getConsumption();
      } else {
        this.labelButtonDisplay = "DisplayDay";//trad
        this.getConsumptionOfMonth();
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
  //z-index: 9999;
}
.topbutton {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  margin-top: 50px;
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

.middlebutton {
  display: flex;
  align-items: center;
  padding: 0 50px;
  margin-top: 25px;
}

.table{
  display: flex;
  flex-direction: column;
  min-height: 60px;
  height: 150px;
  min-width: 60px;
  width: 700px;
  overflow-x: scroll;
}

.row {
  display: flex;
  flex-direction: row;
}

.cellule {
  text-align: center;
  border: 1px solid black;
  padding: 25px;
  font-weight: 1000;
  font-size: 17px;
  width: 150px;
}

.arrow-button {
  padding: 10px 20px;
  background: #e8e8e8;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  align-items: center;
  justify-content: center;
  height: 100px;
  width: 150px;
}

.arrow {
  font-size: 44px;
  margin-left: 10px;
  color: #34c98eff;
}
</style>
