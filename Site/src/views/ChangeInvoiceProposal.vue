<template>
  <div class="main">
      <div class="header">
        <MainHeader text="header.changeproposal"/>
      </div>
      <div class="content">
          <p><b>{{ $t("invoice.actualproposal") }}</b></p>
          <InputMain :text="$t('invoice.proposal')" v-model="proposal"/>
      </div>
      <div class="bottombuttons">
          <div class="change">
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

export default {
    components: {
        InputMain,
        MainHeader,
        GoButton
    }
}
</script>