<template>
  <div class="main">
      <div class="header">
        <MainHeader text="header.modifyproposal"/>
      </div>
      <div class="content">
          <p><b>{{ $t("invoices.actualproposal") }}</b> : {{proposal}} €</p>
          <InputMain :text="$t('invoices.proposal')" v-model="new_proposal"/>
      </div>
      <div class="bottombuttons">
          <div class="change" @click.prevent.left="changeProposal()">
              <GoButton text="button.change" :colore="'#34c98e'"/>
          </div>
          <div class="backbutton" @click.prevent.left="back()">
              <GoButton text="button.back" :colore="'darkblue'"/>
          </div>
      </div>
  </div>
</template>
<script>
import InputMain from "@/components/InputMain.vue";
import MainHeader from "@/components/MainHeader.vue";
import GoButton from "@/components/GoButton.vue";
import Swal from "sweetalert2";
import GlobalMethods from "@/components/GlobalMethods.vue";

export default {
    components: {
        InputMain,
        MainHeader,
        GoButton
    },
    data() {
        return {
            proposal: sessionStorage.getItem("proposal"),
            price: sessionStorage.getItem("price"),
            invoiceId: sessionStorage.getItem("invoice_id"),
            new_proposal: 0
        }
    },
    created() {
        GlobalMethods.getCurrentLanguage();
    },
    methods: {
        back() {
            sessionStorage.removeItem("proposal");
            sessionStorage.removeItem("price")
            this.$router.push({name: 'InvoiceFull'})
        },
        changeProposal() {
            if(this.isWithinRange()) {
                const requestOptions = {
                    method: 'POST',
                    headers: {"Authorization": this.$cookies.get("token")},
                    body: JSON.stringify({proposal: parseFloat(this.new_proposal), invoice_id: this.invoiceId})
                };
                fetch("https://babawallet.alwaysdata.net/api/client/invoices/proposal", requestOptions)
                    .then(response => {
                        if (!response.ok)
                            throw new Error(response.error);
                        else {
                            Swal.fire({
                                icon: 'success',
                                title: this.$t("alerts.good"),
                                text: this.$t("alerts.proposalchanged"),
                            })
                            this.$router.push({name: 'InvoiceFull'})
                        }
                    }).catch(error => {
                    if (error.message === "error.unauthorizedAccess")
                        GlobalMethods.errorToken();
                    else {
                        GlobalMethods.errorApi(error.message);
                    }
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: this.$t("alerts.error"),
                    text: this.$t("alerts.proposalrange"),
                })
            }
        },
        isWithinRange() {
            this.new_proposal = parseFloat(this.new_proposal);
            let limit = Math.floor(this.price/12)
            const lowerLimit = limit * 0.8;
            const upperLimit = limit * 1.2;
            return this.new_proposal >= lowerLimit && this.new_proposal <= upperLimit;
        }
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