pragma solidity ^0.5.4;

contract SimpleContract{
    uint8 public integer;
    
    constructor() public{
        integer = 0;
    }
    
    function set(uint8 _integer) public{
        integer = _integer;
    }
    function get() public view returns(uint8){
        return integer;
    }
}
