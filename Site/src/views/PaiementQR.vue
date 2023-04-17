<template>
  <div class="main">
      <div class="header">
        <MainHeader text="header.paiement"/>
      </div>
      <div class="qrcode">
          <canvas id="canvas"></canvas>
      </div>
      <div class="bottombuttons">
          <div class="backbutton">
              <GoButton text="button.back" :colore="'darkblue'"/>
          </div>
      </div>
  </div>
</template>

<script>
import MainHeader from "@/components/MainHeader.vue";
import GoButton from "@/components/GoButton.vue";
import QRCode from 'qrcode';

export default {
    components: {
        MainHeader,
        GoButton
    },
    mounted() {
        this.generatePayconiqQRCode();
    },
    methods: {
        generatePayconiqQRCode() {
            const amount = '5.00'; // Montant de 5 euros
            const url = `payconiq://v1.0/transaction?amount=${amount}`; // URL Payconiq avec le montant
            QRCode.toCanvas(document.getElementById('canvas'), url, function (error) {
                if (error) console.error(error);
                console.log('QR code generated successfully');
            });
        }
    }
}
</script>