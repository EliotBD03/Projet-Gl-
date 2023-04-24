<template>
  <div class="main">
      <div class="header">
        <MainHeader text="header.modifyproposal"/>
      </div>
      <div class="content">
          <p><b>{{ $t("invoices.actualproposal") }}</b></p>
          <InputMain :text="$t('invoice.proposal')" v-model="proposal"/>
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
            invoiceId: sessionStorage.getItem("invoice_id")
        }
    },
    methods: {
        back() {
            sessionStorage.removeItem("proposal")
            this.$router.push({name: 'InvoiceFull'})
        },
        changeProposal() {
            const requestOptions = {
                method: 'PUT',
                headers: { "Authorization": this.$cookies.get("token")},
                body: JSON.stringify({ proposal: this.proposal, invoice_id: this.invoiceId })
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
                    if(error.message === "error.unauthorizedAccess")
                        GlobalMethods.errorToken();
                    else {
                        GlobalMethods.errorApi(error.message);
                    }
            });
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