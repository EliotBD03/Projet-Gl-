<template>
  <div class="main">
      <div class="header">
          <MainHeader text="header.paiementinformations"/>
      </div>
      <div class="content">
          <p>
              <InputMain :text="$t('invoice.name')" v-model="name"/>
          </p>
          <p>
              <InputMain :text="$t('invoice.number')" v-model="number"/>
          </p>
          <p>
              <InputMain :text="$t('invoice.expirationdate')" v-model="expiration"/>
          </p>
          <p>
              <InputMain :text="$t('invoice.paiementdate')" v-model="paiementdate"/>
          </p>
          <div class="change" @click.prevent.left="change()">
              <GoButton text="button.change" :colore="'#34c98e'"/>
          </div>
      </div>
  </div>
</template>
<script>
import InputMain from "@/components/InputMain.vue";
import MainHeader from "@/components/MainHeader.vue";
import GoButton from "@/components/GoButton.vue";

export default {
    components: {
        InputMain,
        MainHeader,
        GoButton
    }
}
</script>