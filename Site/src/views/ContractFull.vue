<template>
    <div class="main">
        <div class="header">
            <MainHeader :text="proposal.proposalName"/>
        </div>
        <div class="list">
            <p>Information : </p>
            <p>Provider : {{ proposal.nameProvider }}</p>
            <p>Energy : {{ proposal.typeEnergy }}</p>
        </div>
    </div>
</template>