<template>
    <div class="main">
        <div class="header">
            <MainHeader text="header.consumption"/>
            <div class = "permission"> 
              <p v-if="permission == 'R' && role == 'client'">{{ $t("GestionExtClaire.R") }}</p> 
              <p v-else-if="permission == 'RW' && role == 'client'">{{ $t("GestionExtClaire.RW") }}</p> 
              <p v-else-if="permission == null && role == 'client'">{{ $t("GestionExtClaire.gestion") }}</p>
            </div>
        </div>
        <div class="topbutton">
            <div @click.prevent.left="changeMode(false)">
                <GoButton text="button.table" :colore="'#34c98e'"/>
            </div>
            <div @click.prevent.left="exportData()">
                <GoButton text="button.export" :colore="'#34c98e'"/>
            </div>
            <div v-if="permission != 'R'">
              <input type="file" id="csv-file" accept=".csv"/>
              <div @click.prevent.left="importData()">
                  <GoButton text="button.import" :colore="'#34c98e'"/>
              </div>
            </div>
            <div @click.prevent.left="changeMode(true)">
                <GoButton text="button.graphic" :colore="'#34c98e'"/>
            </div>
        </div>
        <div class="middlebutton">
            <div style="display: block;">
                <button class="arrow-button" @click.prevent.left="getDataBefore(true)">
                    <span class="arrow" style="color: #34c98eff;">&larr;</span>
                </button>
                <button class="arrow-button" @click.prevent.left="getDataBefore(false)" id="arrowl2" style="display: none; margin-top: 10px;">
                    <span class="arrow" style="color: #C04BC0">&larr;</span>
                </button>
            </div>
            <div class="infos">
                <div class="container">
                        <canvas ref="myChart" id="chart"></canvas>
                        <canvas ref="myChart2" id="chart2" style="display: none;"></canvas>
                    <div class="tableH" id="table" style="display: none;">
                        <div>
                            <div v-for="(date, index) in listDate" :key="index" class="cellule">
                                {{ date }}
                            </div>
                        </div>
                        <div>
                            <div v-for="(data, index) in listValue" :key="index" class="cellule">
                                {{ data }}
                            </div>
                        </div>
                    </div>
                    <div id="stat" style="display: none;">
                        <div v-if="listValue.length > 0">
                          <p>{{ $t('consumptions.sum') }} : {{ stat[0] }}</p>
                          <p>{{ $t('consumptions.mean') }} : {{ stat[1] }}</p>
                          <p>{{ $t('consumptions.standardDeviation') }} : {{ stat[2] }}</p>
                          <p>{{ $t('consumptions.median') }} : {{ stat[3] }}</p>
                          <p>{{ $t('consumptions.quartiles') }} : {{ stat[4] }}</p>
                          <p>{{ $t('consumptions.interquartileRange') }} : {{ stat[5] }}</p>
                          <p>{{ $t('consumptions.minimum') }} : {{ stat[6] }}</p>
                          <p>{{ $t('consumptions.maximum') }} : {{ stat[7] }}</p>
                        </div>
                        <div v-else>
                          <p>{{ $t('Il faut des données de consommations') }}</p>
                        </div>
                    </div>
                </div>
                <div class="newconsumption" >
                    <div v-if="permission != 'R'" class="newconsumption" >
                        <InputMain type="date" id="dateNewConsumption" value="2020-01-01" min="2020-01-01" max="2099-12-31"/>
                        <InputMain type="number" id="dataNewConsumption" min="0" step="0.01" />
                        <div @click.prevent.left="post()">
                            <GoButton type="submit" text="button.add" :colore="'#34c98e'"/>
                        </div>
                    </div>
                </div>
            </div>
            <div style="display: block;">
                <button class="arrow-button" @click.prevent.left="getDataAfter(true)">
                    <span class="arrow" style="color: #34c98eff;">&rarr;</span>
                </button>
                <button class="arrow-button" @click.prevent.left="getDataAfter(false)" id="arrowr2" style="display: none; margin-top: 10px;">
                    <span class="arrow" style="color: #C04BC0">&rarr;</span>
                </button>
            </div>
        </div>
        <div class="bottombutton">
            <div @click.prevent.left="back()">
                <GoButton text="button.back" :colore="'darkblue'"/>
            </div>
            <select v-on:change="changeModSeeConsumption($event.target.value)">
                <option value="justSee">{{ $t('consumptions.justSee') }}</option>
                <option value="compareWithOther">{{ $t('consumptions.compareWithOther') }}</option>
                <option value="compareOverTime">{{ $t('consumptions.compareOverTime') }}</option>
                <option value="statistic">{{ $t('consumptions.statistic') }}</option>
            </select>
            <div id="modeTime" @click.prevent.left="changeModTime()">
                <GoButton :text=labelButtonDisplay :colore="'#34c98e'"/>
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
import VueCookies from 'vue-cookies';
const cookies = VueCookies;

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
            permission : sessionStorage.getItem("permission"), //Extension Claire
            date : "",
            date2 : "",
            isAfter : true,
            listDate : [],
            listValue : [],
            listValueOther : [],
            listDate2 : [],
            listValue2 : [],
            modConso : "justSee",
            listNewValue : [],
            listNewDate : [],
            forcing : false,
            chart : null,
            chart2 : null,
            isDisplayDay : true,
            month : "",
            year : "",
            month2 : "",
            year2 : "",
            unity : "",
            labelButtonDisplay : "",
            stat : [0, 0, 0, 0, [], 0, 0, 0],
            tmpFlag : false,
            role : this.$cookies.get("role")
        }},

    created() {
        this.get("unity");
        this.get("consumption");
        GlobalMethods.getCurrentLanguage();
        this.labelButtonDisplay = this.$t("consumptions.displaymonth") + this.unity;
    },
    methods: {
        showData() { // Méthode qui permet d'afficher/actualiser les données
            this.mode ? this.showGraphic() : this.showTable();
        },

        changeMode (mode) { // Méthode qui permet de changer le mode d'affichage
            if(this.mode == mode) return;
            this.mode = mode;
            this.showData();
        },

        exportData() { // Méthode qui permet d'exporter les données actuelles affichées à l'écran. Le fichier est au format csv
            const table = [];
            table.push(["date", "data" + this.unity]);
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

        importData() { // Méthode qui permet d'importer des données via un fichier csv directement dans le base de données
            this.listNewDate = [];
            this.listNewValue = [];

            const dateRegex = /\d{4}-\d{2}-\d{2}/;

            const fileInput = document.getElementById('csv-file');
            if(fileInput.files.length > 0) {
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
            }
        },

        async getDataBefore(isMyConsumption) { // Méthode qui permet de changer la date de recherche pour en suite aller chercher de nouvelles données dans le passé
            if(isMyConsumption) { // Si on veut voir plus de ses données pour les autres données
              if(this.isDisplayDay) {
                  this.isAfter = false;
                  this.date = this.listDate[0];
                  if(this.modConso == "compareOverTime") { // Si on est en traine de comparer deux graphiques il faut temporairement changer le mod pour que les consos aillent dans la bonne liste
                    this.modConso = "justSee";
                    this.tmpFlag = true;
                  }
                  await this.get("consumption");
              } else {
                  this.month--;
                  if(this.month <= 0) {
                      this.month = 12;
                      this.year--;
                  }
                  if(this.modConso == "compareOverTime") {
                    this.modConso = "justSee";
                    this.tmpFlag = true;
                  }
                  await this.get("consumptionOfMonth");
              }

              if(this.modConso == "compareWithOther") { // Si on récupère des données de l'autre consommation jusqu'à ce qu'on ait le même nombre de données
                  while(this.listValue.length > this.listValueOther.length) {
                      await this.get("consumptionOther");
                  }
              }
            } else {
                if(this.isDisplayDay) {
                    this.isAfter = false;
                    this.date2 = this.listDate2[0];
                    this.get("consumption");
                } else {
                    this.month2--;
                    if(this.month2 <= 0) {
                        this.month2 = 12;
                        this.year2--;
                    }
                    this.get("consumptionOfMonth");
                }
            }
        },

        async getDataAfter(isMyConsumption) { // Méthode qui permet de changer la date de recherche pour en suite aller chercher de nouvelles données dans le futur
            if(isMyConsumption) {
              if(this.isDisplayDay) {
                  this.isAfter = true;
                  this.date = this.listDate.slice(-1)[0];
                  if(this.modConso == "compareOverTime") {
                    this.modConso = "justSee";
                    this.tmpFlag = true;
                  }
                  await this.get("consumption");
              } else {
                  this.month++;
                  if(this.month >= 13) {
                      this.month = 1;
                      this.year++;
                  }
                  if(this.modConso == "compareOverTime") {
                    this.modConso = "justSee";
                    this.tmpFlag = true;
                  }
                    await this.get("consumptionOfMonth");
              }

              if(this.modConso == "compareWithOther") {
                  while(this.listValue.length > this.listValueOther.length) {
                      await this.get("consumptionOther");
                  }
              }
            } else {
              if(this.isDisplayDay) {
                  this.isAfter = true;
                  this.date2 = this.listDate2.slice(-1)[0];
                  this.get("consumption");
              } else {
                  this.month2++;
                  if(this.month2 >= 13) {
                      this.month2 = 1;
                      this.year2++;
                  }
                  this.get("consumptionOfMonth");
              }
            }
        },

        showTable() { // Méthode qui détruit le graphique pour afficher ensuite le tableau
            if(!this.mode && this.modConso == "justSee") {
                this.chart.destroy();
                document.getElementById("table").style.display = "flex";
            }
        },

        showGraphic() { // Méthode qui cache le tableau pour en suite préparer et afficher le graphique
            if(this.mode && this.modConso != "statistic") {
                document.getElementById("table").style.display = "none";

                const ctx = this.$refs.myChart.getContext('2d');
                const data = {
                    labels: this.listDate,
                    datasets: [{
                        label: this.$t("consumptions.yourconsumption") + this.unity,
                        data: this.listValue,
                        fill: false,
                        borderColor: 'rgb(75, 192, 192)',
                        tension: 0.1
                    }]
                };

                if(this.modConso == "compareWithOther") {
                    data.datasets.push({
                        label: this.$t("consumptions.otherconsumption"),
                        data: this.listValueOther,
                        fill: false,
                        borderColor: 'rgb(192, 75, 192)',
                        tension: 0.1
                    });
                }

                if(this.chart) {
                    this.chart.destroy();
                }

                this.chart = new Chart(ctx, {
                    type: 'line',
                    data: data,
                });

                this.chart.update();

                if (this.modConso == "compareOverTime") {
                  const ctx2 = this.$refs.myChart2.getContext("2d");
                  const data2 = {
                    labels: this.listDate2,
                    datasets: [
                      {
                        label: this.$t("consumptions.yourconsumption") + this.unity,
                        data: this.listValue2,
                        fill: false,
                        borderColor: "rgb(192, 75, 192)",
                        tension: 0.1,
                      },
                    ],
                  };

                  if (this.chart2) {
                    this.chart2.destroy();
                  }

                  this.chart2 = new Chart(ctx2, {
                    type: "line",
                    data: data2,
                  });

                  this.chart2.update();
                }
            }
        },

        checkArgs() { // Méthode qui permet de vérifier que l'utilisateur a bien entré toutes les données néccéssaire pour ajouter une valeur
            if(this.listNewDate.length > 0 && this.listNewValue.length == this.listNewDate.length) {
                return true;
            }

            const date = document.getElementById("dateNewConsumption").value;
            const data = document.getElementById("dataNewConsumption").value;
            if(data == '')
            {
                Swal.fire(this.$t("alerts.entervalue"));
                return false;
            }
            else if(data < 0)
            {
                Swal.fire(this.$t("alerts.enterpositiveconsumption"));
                return false;
            }

            this.listNewDate.push(date);
            this.listNewValue.push(data);

            return true;
        },

        async get(typeRequest) { // Méthode qui permet de faire une requête vers l'API en fonction du type de requête
            const requestOptions = {
                method: "GET",
                headers: {'Authorization' : this.$cookies.get("token")},
            };
            try {
                let way = ``;
                let dateWay = "";
                let month = 1;
                switch(typeRequest) {
                    case "unity":
                        way = `https://babawallet.alwaysdata.net/api/common/contracts/type_of_energy/ + ${sessionStorage.getItem('contractId')}`;
                        break;
                    case "consumption":
                        if(this.modConso == "justSee" || this.modConso == "compareWithOther") {
                          if(this.date != "") {
                              dateWay = `&date=${this.date}`;
                          }
                          else {
                              this.isAfter = false;
                          }
                        } else if(this.modConso == "compareOverTime") {
                          if(this.date2 != "") {
                              dateWay = `&date=${this.date2}`;
                          }
                          else {
                              this.isAfter = false;
                          }
                        }

                        way = `https://babawallet.alwaysdata.net/api/common/consumptions/${this.ean}?is_after=${this.isAfter}${dateWay}`;
                        break;
                    case "consumptionOfMonth":
                        if(this.modConso == "justSee" || this.modConso == "compareWithOther") {
                          if(this.year != "" && this.month != "") {
                              dateWay = `?year=${this.year}&month=${this.month}`;
                          }
                        } else {
                          if(this.year2 != "" && this.month2 != "") {
                              dateWay = `?year=${this.year2}&month=${this.month2}`;
                          }
                        }
                        way = `https://babawallet.alwaysdata.net/api/common/consumptions_month/${this.ean}${dateWay}`;
                        break;
                    case "consumptionOther":
                        if(this.listDate.length > 0) {
                            if(this.isAfter) {
                              month = parseInt(this.listDate[this.listDate.length-1].split("-")[1], 10).toString().padStart(2, '0');
                            } else {
                              month = parseInt(this.listDate[0].split("-")[1], 10).toString().padStart(2, '0');
                            }
                        }
                        way = `https://babawallet.alwaysdata.net/api/common/other_consumptions/${sessionStorage.getItem('contractId')}/${month}`;
                        break;
                }

                const response = await fetch(way, requestOptions);
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
                    
                    switch(typeRequest) {
                        case "unity":
                            if(data.type_of_energy == "water") {
                                this.unity = " (m³)";
                            } else {
                                this.unity = " (kWh)";
                            }

                            this.labelButtonDisplay = this.$t("consumptions.displaymonth") + this.unity;
                            break;

                        case "consumption":
                            if(!(Object.keys(data.listConsumption).length === 0)) {
                                let keys = Object.keys(data.listConsumption);
                                let values = Object.values(data.listConsumption);

                                if(this.modConso != "compareOverTime") {
                                  if(this.isAfter) {
                                      this.listDate = this.listDate.concat(keys);
                                      this.listValue = this.listValue.concat(values);

                                      if(this.listDate.length > 30) {
                                          this.listDate = this.listDate.slice(10);
                                          this.listValue = this.listValue.slice(10);
                                      }
                                  } else {
                                      this.listDate = keys.concat(this.listDate);
                                      this.listValue = values.concat(this.listValue);

                                      if(this.listDate.length > 30) {
                                          this.listDate = this.listDate.slice(0, 30);
                                          this.listValue = this.listValue.slice(0, 30);
                                      }
                                  }
                                } else {
                                  if(this.isAfter) {
                                      this.listDate2 = this.listDate2.concat(keys);
                                      this.listValue2 = this.listValue2.concat(values);

                                      if(this.listDate2.length > 30) {
                                          this.listDate2 = this.listDate2.slice(10);
                                          this.listValue2 = this.listValue2.slice(10);
                                      }
                                  } else {
                                      this.listDate2 = keys.concat(this.listDate2);
                                      this.listValue2 = values.concat(this.listValue2);

                                      if(this.listDate2.length > 30) {
                                          this.listDate2 = this.listDate2.slice(0, 30);
                                          this.listValue2 = this.listValue2.slice(0, 30);
                                      }
                                  }
                                }
                            }
                            break;

                        case "consumptionOfMonth":
                            if(this.modConso != "compareOverTime") {
                                this.listDate = Object.keys(data.listConsumption);
                                this.listValue = Object.values(data.listConsumption);

                                if(this.listDate.length > 0) {
                                    this.year = this.listDate[0].slice(0, 4);
                                    this.month = this.listDate[0].slice(5, 7);
                                }
                            } else {
                                this.listDate2 = Object.keys(data.listConsumption);
                                this.listValue2 = Object.values(data.listConsumption);

                                if(this.listDate2.length > 0) {
                                    this.year2 = this.listDate2[0].slice(0, 4);
                                    this.month2 = this.listDate2[0].slice(5, 7);
                                }
                            }
                            break;

                        case "consumptionOther":
                            if(this.isAfter) {
                              this.listValueOther = this.listValueOther.concat(Object.values(data.listConsumption));
                              this.listValueOther = this.listValueOther.slice(0, this.listValue.length);
                            } else {
                              this.listValueOther = Object.values(data.listConsumption).concat(this.listValueOther);
                              this.listValueOther = this.listValueOther.slice(0, this.listValue.length);
                            }
                            break;
                    }

                    if(this.tmpFlag) {
                      this.modConso = "compareOverTime";
                      this.tmpFlag = false;
                    }

                    if(this.modConso == "statistic") {
                        this.calculStat();
                    } else {
                        this.showData();
                    }
                }
            } catch(error) {
                if(error.message === "Token") {
                    this.$cookies.remove("token");
                    this.$cookies.remove("role");
                    Swal.fire(this.$t("alerts.connectionexpired"));
                    this.$router.push("/");
                }
                else {
                    GlobalMethods.errorApi(error.message);
                }
            }
        },

        async post (){ // Méthode qui permet d'ajouter des données de consommations
            if(this.checkArgs())
            {
                const requestOptions = {
                    method: "POST",
                    headers: {'Authorization': this.$cookies.get("token")},
                    body: JSON.stringify({ ean: this.ean, list_value: this.listNewValue, list_date: this.listNewDate, forcing: this.forcing})
                };
                try {
                    const response = await fetch(`https://babawallet.alwaysdata.net/api/common/consumptions`, requestOptions)
                    if (!response.ok) {
                        if(response.status == 401) {
                            throw new Error("Token");
                        } else {
                            const data = await response.json();
                            throw new Error(data.error);
                        }
                    } else {
                        const data = await response.json();
                        if(data.valueChange) {
                            Swal.fire({
                                icon: 'success',
                                title: this.$t("alerts.good"),
                                text: this.$t("alerts.addedconsumption")
                            })
                            this.listNewValue = [];
                            this.listNewDate = [];

                            this.listDate = [];
                            this.listValue = [];

                            if(this.isDisplayDay) {
                                this.date = "";
                                this.get("consumption");
                            } else {
                                this.year = "";
                                this.month = "";
                                this.get("consumptionOfMonth");
                            }
                        } else {
                            Swal.fire({
                                title: this.$t("alerts.surechangingvalue"),
                                text: this.$t("alerts.cantreturn"),
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
                } catch(error) {
                    if(error.message === "Token") {
                        GlobalMethods.errorToken();
                    }
                    else {
                        GlobalMethods.errorApi(error.message);
                    }
                }
            }
        },

        async changeModTime() { // Méthode qui permet de changer le type d'affichage. Soit jour par jour, soit par moi
            this.isDisplayDay = !this.isDisplayDay;
            this.listDate = [];
            this.listValue = [];

            if(this.isDisplayDay) {
                this.labelButtonDisplay = this.$t("consumptions.displaymonth") + this.unity;
                await this.get("consumption");
                if(this.modConso == "compareOverTime") {
                    this.modConso = "justSee";
                    this.tmpFlag = true;
                    await this.get("consumption");
                }
            } else {
                this.labelButtonDisplay = this.$t("consumptions.displayday") + this.unity;
                  await this.get("consumptionOfMonth");

                  while(this.listValue.length > this.listValueOther.length) {
                    await this.get("consumptionOther");
                  }

                  if(this.modConso == "compareOverTime") {
                      this.modConso = "justSee";
                      this.tmpFlag = true;
                      await this.get("consumptionOfMonth");
                  }
            }
        },

        back() { // Méthdode qui permet de revenir à la page précédente en fonction de son rôle
            if(cookies.isKey("token") && cookies.isKey("role"))
            {
                if(cookies.get("role") === 'client' && this.permission != 'RW' && this.permission != 'R'){
                    sessionStorage.clear();
                    this.$router.push('/wallets');
                }
                else if(cookies.get("role") === 'client' && this.permission != null){
                    sessionStorage.clear();
                    this.$router.push('/invitedwallets');
                }
                else{
                    sessionStorage.removeItem('ean');
                    sessionStorage.removeItem('contractId');
                    this.$router.push('/clientFull');
                }
            }
        },
 
        async changeModSeeConsumption(mod) { // Méthode qui permet de changer la perception des données en différents modes. (seul,comparer ses données avec celles d'un autre utilisateur, comparer ses données à plusieurs moments dans le temps
            if(mod == this.modConso) return;

            this.modConso = mod;
            this.listValueOther = [];
            this.listDate2 = [];
            this.listValue2 = [];
            
            document.getElementById("chart").style.display = "none";
            document.getElementById("chart2").style.display = "none";
            document.getElementById("arrowl2").style.display = "none";
            document.getElementById("arrowr2").style.display = "none";
            document.getElementById("stat").style.display = "none";

            switch(mod) {
                case "justSee":
                    this.showData();
                    break;

                case "compareWithOther":
                    document.getElementById("chart").style.display = "inline";
                    while(this.listValue.length > this.listValueOther.length) {
                        await this.get("consumptionOther");
                    }
                    break;

                case "compareOverTime":
                    document.getElementById("chart").style.display = "inline";
                    document.getElementById("chart2").style.display = "inline";
                    document.getElementById("arrowl2").style.display = "inline";
                    document.getElementById("arrowr2").style.display = "inline";

                    if(this.isDisplayDay) {
                        this.get("consumption");
                    } else {
                        this.get("consumptionOfMonth");
                    }
                    break;

                case "statistic":
                    document.getElementById("stat").style.display = "block";
                    this.calculStat();
                    break;
            }
        },

        calculStat() { // Méthode qui permet de calculer plusieurs statistiques à partir des données de consommation de l'utilisateur
            this.stat = [0, 0, 0, 0, [], 0, 0, 0];
            if(this.listValue.length == 0 ) return;

            // La somme
            this.stat[0] = this.listValue.reduce((acc, curr)=>{ return acc + curr }, 0);

            // La moyenne
            this.stat[1] = this.stat[0] / this.listValue.length;

            // L'écart type
            let tmp = this.listValue.map((k)=>{return (k - this.stat[1]) ** 2})
            let sum = tmp.reduce((acc, curr)=> acc + curr, 0);
            this.stat[2] = Math.sqrt(sum / tmp.length);

            // La médiane
            let values = Array.from(this.listValue);
            values.sort(function(a,b){return a-b;});
            var half = Math.floor(values.length / 2);
            if (values.length % 2)
              return values[half];
            this.stat[3] = (values[half - 1] + values[half]) / 2.0;

            // Les quartiles
            if (this.listValue.length >= 3) {
              values = Array.from(this.listValue);
              values.sort(function(a, b) {return a - b;});

              this.stat[4].push(values[Math.floor(values.length * 0.25)]);
              this.stat[4].push(values[Math.floor(values.length * 0.50)]);
              this.stat[4].push(values[Math.floor(values.length * 0.75)]);

              // L'écart interquartile
              if (this.stat[4].length >= 2) {
                this.stat[5] = this.stat[4][2] - this.stat[4][0];
              }
            }

            // Le minimum
            this.stat[6] = Math.min(...this.listValue);
            // Le maximum
            this.stat[7] = Math.max(...this.listValue);
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
    margin-top: 20px;
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
    flex-direction: column;
    width: 1000px;
    max-width: 1000px;
    height: 400px;
    background: rgb(236, 236, 236);
    box-shadow: rgba(0, 0, 0, 0.4) 0px 2px 4px, rgba(0, 0, 0, 0.3) 0px 7px 13px -3px, rgba(0, 0, 0, 0.2) 0px -3px 0px inset;
}

.container > *{
    display: block;
    width: 40%;
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
    padding: 0 5px;
    margin-top: 5px;
}

.middlebutton {
    display: flex;
    align-items: center;
    padding: 0 50px;
    margin-top: 25px;
}
.tableH{
    display: flex;
    height: 300px;
    width: 700px;
    overflow-x: auto;
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
}

select {
  padding: 15px 100px;
  border: unset;
  border-radius: 15px;
  color: #212121;
  background: #e8e8e8;
  font-weight: 1000;
  font-size: 17px;
  box-shadow: 4px 8px 19px -3px rgba(0,0,0,0.27);
  margin: 10px;
  text-align: center;
}

.permission{
  position: fixed;
  margin-top: 20px;
  margin-right: 20px;
  top: 0;
  right: 0;
  z-index: 9999;
  font-size: 25px;
}


</style>
