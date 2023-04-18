<template>
  <div class="main">
      <div class="header">
        <MainHeader text="header.invoice"/>
      </div>
      <div class="allcards">
          <div class="cards" v-for="invoice in invoicesList" :key="invoice.id">
              <p class="number"> {{ invoice.id }}</p>
              <p><b>{{ $t("invoices.remaining") }}</b></p>
              <p>{{ invoice.account }}</p>
              <div v-if="invoice.status">
                  <div class="payed">
                      <GoButton text="button.payed" :colore="'orange'"/>
                  </div>
              </div>
              <div v-else>
                  <div class="notpayed" @click.prevent.left="seeMore(invoice)">
                      <GoButton text="button.go" :colore="'#34c98e'"/>
                  </div>
              </div>
          </div>
      </div>
      <div class="bottombuttons">
          <div class="homebutton">
              <GoButton text="header.home" :colore="'#B1B9FC'"/>
          </div>
      </div>
  </div>
</template>

<script>
import MainHeader from "@/components/MainHeader.vue";
import GoButton from "@/components/GoButton.vue";
export default {
    components: {
        MainHeader,
        GoButton
    },
}
</script>

<style scoped>


</style>