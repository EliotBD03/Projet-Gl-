<template>
  <div class="main">
      <div class="header">
          <MainHeader text="header.paiementinformations"/>
      </div>
      <div class="content">
          <p>

              <InputMain :text="$t('invoices.name')" v-model="name"/>
          </p>
          <p>
              <InputMain :text="$t('invoices.number')" v-model="number"/>
          </p>
          <p>
              <InputMain :text="$t('invoices.expirationdateplaceholder')" v-model="expiration"/>
          </p>
          <p>
              <input type="radio" id="Auto" value="0" v-model="paymentmethod">
              <label for="Auto">{{ $t("invoices.auto") }}</label>
              <input type="radio" id="Manual" value="1" v-model="paymentmethod">
              <label for="Variable">{{ $t("invoices.manual") }}</label>
          </p>
          <div class="change" @click.prevent.left="post()">
              <GoButton text="button.change" :colore="'#34c98e'"/>
          </div>
      </div>
  </div>
</template>
<script>
import InputMain from "@/components/InputMain.vue";
import MainHeader from "@/components/MainHeader.vue";
import GoButton from "@/components/GoButton.vue";
import GlobalMethods from "@/components/GlobalMethods.vue";
import Swal from "sweetalert2";

export default {
    components: {
        InputMain,
        MainHeader,
        GoButton
    },
    data() {
        return {
            client_id: sessionStorage.getItem("client_id"),
            name: "",
            number: "",
            expiration: "",
            paymentmethod: "",
        }
    },
    created() {
        this.getBank();
    },
    methods: {
        async getBank() {
            const requestOptions = {
                method: "GET",
                headers: {'Authorization': this.$cookies.get("token")},
            };
            try {
                const response = await fetch("https://babawallet.alwaysdata.net/api/client/invoices/account/" + this.client_id, requestOptions);
                if(!response.ok) {
                    const data = await response.json();
                    throw new Error(data.error);
                } else {
                    const data = await response.json()
                    this.bank = data.bank;
                    if(this.bank === null) {
                        this.name = "";
                        this.number = "";
                        this.expiration = "";
                        this.paymentmethod = "";
                    } else {
                        this.name = this.bank.accountName;
                        this.number = this.bank.accountNumber;
                        this.expiration = this.bank.expirationDate;
                        this.paymentmethod = this.bank.paymentMethod;
                    }
                }
            } catch (error) {
                if (error.error === "error.unauthorizedAccess") {
                    GlobalMethods.errorToken();
                } else {
                    GlobalMethods.errorApi(error.message);
                }
            }
        },
        post(){
            const requestOptions = {
                method: "PUT",
                headers: {'Authorization': this.$cookies.get("token")},
                body: JSON.stringify({
                    account_name: this.name,
                    account_number: this.number,
                    expiration_date: this.expiration,
                    payment_method: this.paymentmethod
                })
            };
            try {
                fetch("https://babawallet.alwaysdata.net/api/client/invoices/"+this.client_id+"/account/", requestOptions)
                    .then(response => {
                        if(!response.ok) {
                            return response.json().then(json => Promise.reject(json));
                        }
                    })
                    .then(
                        this.$router.push({name: "InvoiceFull"})
                    )
                    .then(
                        Swal.fire({
                            icon: 'success',
                            title: this.$t("alerts.good"),
                            text: this.$t("alerts.accountinformationchanged"),
                        })
                    )
            } catch (error) {
                if (error.error === "error.unauthorizedAccess") {
                    GlobalMethods.errorToken();
                } else {
                    GlobalMethods.errorApi(error.message);
                }
            }
        },

    }

}
</script>

<style scoped>
.main{
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    align-items: center;
    height: 100vh;
}

.content{
    display: flex;
    align-items: center;
    justify-content: space-evenly;
    flex-direction: column;
    width: 600px;
    border-radius: 50px;
    background: #e0e0e0;
    box-shadow: 0 15px 50px rgba(177, 185, 252, 1);
}
</style>